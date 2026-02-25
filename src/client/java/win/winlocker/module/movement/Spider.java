package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.misc.StopWatch;
import win.winlocker.utils.player.PlayerIntersectionUtil;

import java.util.List;

/**
 * Spider - позволяет подниматься по вертикальным поверхностям
 */
public class Spider extends Module {
    private final ModeSetting mode;
    private final SliderSetting boost;
    private final SliderSetting delay;
    private final BooleanSetting onlyOnCollision;
    private final BooleanSetting debug;
    private final SliderSetting fallDistance;

    private final StopWatch timer = new StopWatch();
    private final StopWatch placeTimer = new StopWatch();
    private boolean wasOnGround;
    private int lastSlot = -1;

    public Spider() {
        super("Spider", Category.MOVEMENT);

        this.mode = new ModeSetting("Mode", "FunTime", List.of("FunTime", "FunTime2", "Vanilla", "NCP"));
        this.boost = new SliderSetting("Boost", 0.42, 0.1, 1.0);
        this.delay = new SliderSetting("Delay", 400, 100, 1000);
        this.onlyOnCollision = new BooleanSetting("Only Collision", true);
        this.debug = new BooleanSetting("Debug", false);
        this.fallDistance = new SliderSetting("Fall Distance", 1.5f, 0.5f, 3.0f);

        addSetting(mode);
        addSetting(boost);
        addSetting(delay);
        addSetting(onlyOnCollision);
        addSetting(debug);
        addSetting(fallDistance);
    }

    @Override
    public void onEnable() {
        wasOnGround = false;
        timer.reset();
    }

    @Override
    public void onDisable() {
        timer.reset();
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        // Если игрок нажимает прыжок - не вмешиваемся
        if (mc.options.keyJump.isDown()) {
            return;
        }

        switch (mode.get()) {
            case "FunTime":
                tickFunTime(mc);
                break;
            case "FunTime2":
                tickFunTime2(mc);
                break;
            case "Vanilla":
                tickVanilla(mc);
                break;
            case "NCP":
                tickNCP(mc);
                break;
        }
    }

    /**
     * Режим FunTime - из вашего кода
     */
    private void tickFunTime(Minecraft mc) {
        double boostValue = boost.get();
        long delayValue = (long) delay.get();

        // Уменьшенный бокс игрока для проверки
        AABB playerBox = mc.player.getBoundingBox().inflate(-0.001);

        // Бокс для проверки блока под ногами (высота 0.5)
        AABB checkBox = new AABB(
            playerBox.minX,
            playerBox.minY,
            playerBox.minZ,
            playerBox.maxX,
            playerBox.minY + 0.5,
            playerBox.maxZ
        );

        // Проверяем, прошло ли время с последнего буста
        if (!StopWatch.finished(delayValue)) {
            return;
        }

        // Проверяем коллизию с блоками на уровне ног
        if (PlayerIntersectionUtil.isBox(checkBox, this::hasCollision)) {
            // Есть блок рядом на уровне ног - проверяем, есть ли блок сверху
            AABB topBox = new AABB(
                playerBox.minX,
                playerBox.minY + 1,
                playerBox.minZ,
                playerBox.maxX,
                playerBox.maxY,
                playerBox.maxZ
            );

            if (PlayerIntersectionUtil.isBox(topBox, this::hasCollision)) {
                // Есть блок сверху - мы внутри столба, бустим вверх
                if (debug.get()) {
                    mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Spider: Boost UP (inside pillar)"), true);
                }

                mc.player.setOnGround(true);
                mc.player.setDeltaMovement(0, boostValue, 0);
                timer.reset();
            } else {
                // Нет блока сверху - это последний блок, не бустим
                if (debug.get()) {
                    mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Spider: No boost (top of wall)"), true);
                }
                // Просто стоим или идём вперёд
            }
        }
    }

    /**
     * Режим FunTime2 - с авто-установкой блоков
     */
    private void tickFunTime2(Minecraft mc) {
        if (!mc.player.horizontalCollision) {
            return;
        }

        long delayValue = (long) delay.get();
        double boostValue = boost.get();
        float maxFallDistance = (float) fallDistance.get();

        // Проверяем таймер
        if (!StopWatch.finished(delayValue)) {
            return;
        }

        // Устанавливаем onGround и прыгаем
        mc.player.setOnGround(true);
        mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.42, mc.player.getDeltaMovement().z);
        placeTimer.reset();

        // Если падаем - ставим блоки под себя
        int blockSlot = getBlockSlot(true);
        if (blockSlot != -1 && mc.player.fallDistance > 0 && mc.player.fallDistance < maxFallDistance) {
            placeBlock(blockSlot);
        }
    }

    /**
     * Режим Vanilla - простой Spider без проверок
     */
    private void tickVanilla(Minecraft mc) {
        if (!mc.player.horizontalCollision) {
            return;
        }

        double boostValue = boost.get();

        // Если есть коллизия спереди и мы не на земле - бустим вверх
        if (!mc.player.onGround() && mc.player.horizontalCollision) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, boostValue, mc.player.getDeltaMovement().z);
            mc.player.setOnGround(true);
        }
    }

    /**
     * Режим NCP - более плавный Spider
     */
    private void tickNCP(Minecraft mc) {
        if (!mc.player.horizontalCollision) {
            return;
        }

        double boostValue = boost.get();
        long delayValue = (long) delay.get();

        // Проверяем, можем ли мы подняться
        if (!StopWatch.finished(delayValue)) {
            return;
        }

        // Проверяем блок над головой
        BlockPos above = mc.player.blockPosition().above();
        if (!hasCollision(above)) {
            // Можем подняться
            mc.player.setDeltaMovement(0, boostValue, 0);
            mc.player.setOnGround(true);
            timer.reset();
        }
    }

    /**
     * Проверка на коллизию
     */
    private boolean hasCollision(BlockPos pos) {
        return PlayerIntersectionUtil.hasCollision(pos);
    }

    /**
     * Проверка горизонтальной коллизии
     */
    private boolean hasHorizontalCollision() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return false;
        }

        Vec3 pos = mc.player.position();
        double width = mc.player.getBbWidth() / 2;

        // Проверяем блоки спереди, сзади, слева, справа
        BlockPos[] checkPositions = {
            new BlockPos((int) Math.floor(pos.x + width), (int) pos.y, (int) pos.z),
            new BlockPos((int) Math.floor(pos.x - width), (int) pos.y, (int) pos.z),
            new BlockPos((int) pos.x, (int) pos.y, (int) Math.floor(pos.z + width)),
            new BlockPos((int) pos.x, (int) pos.y, (int) Math.floor(pos.z - width))
        };

        for (BlockPos checkPos : checkPositions) {
            if (hasCollision(checkPos)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Поставить блок
     */
    private void placeBlock(int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        int lastSlot = mc.player.getInventory().selected;
        mc.player.getInventory().selected = slot;

        // Поворот взгляда вниз
        float oldPitch = mc.player.getXRot();
        mc.player.setXRot(80f);

        // Raycast
        Vec3 start = mc.player.getEyePosition(1f);
        Vec3 look = mc.player.getLookAngle();
        Vec3 end = start.add(look.scale(4));

        BlockHitResult hitResult = mc.level.clip(
            new net.minecraft.world.level.ClipContext(
                start,
                end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                mc.player
            )
        );

        if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            Direction side = hitResult.getDirection();

            // Сваг рукой
            mc.player.swing(InteractionHand.MAIN_HAND);

            // Правый клик по блоку
            mc.gameMode.useItemOn(
                mc.player,
                InteractionHand.MAIN_HAND,
                hitResult
            );
        }

        // Возвращаем слот и угол обзора
        mc.player.getInventory().selected = lastSlot;
        mc.player.setXRot(oldPitch);
        mc.player.fallDistance = 0;
    }

    /**
     * Получить слот с блоком для установки
     */
    public int getBlockSlot(boolean inHotBar) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return -1;
        }

        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;

        for (int i = firstSlot; i < lastSlot; i++) {
            var stack = mc.player.getInventory().getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            var item = stack.getItem();
            
            // Проверяем, является ли предмет блоком
            if (item instanceof net.minecraft.world.item.BlockItem) {
                return i;
            }

            // Специфичные предметы (цветы, трава и т.д.)
            if (isPlaceableItem(item)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Проверить, можно ли установить предмет
     */
    private boolean isPlaceableItem(net.minecraft.world.item.Item item) {
        return item == net.minecraft.world.item.Items.POPPY ||
               item == net.minecraft.world.item.Items.BLUE_ORCHID ||
               item == net.minecraft.world.item.Items.ALLIUM ||
               item == net.minecraft.world.item.Items.AZURE_BLUET ||
               item == net.minecraft.world.item.Items.RED_TULIP ||
               item == net.minecraft.world.item.Items.ORANGE_TULIP ||
               item == net.minecraft.world.item.Items.WHITE_TULIP ||
               item == net.minecraft.world.item.Items.PINK_TULIP ||
               item == net.minecraft.world.item.Items.KELP ||
               item == net.minecraft.world.item.Items.OXEYE_DAISY ||
               item == net.minecraft.world.item.Items.LILY_OF_THE_VALLEY ||
               item == net.minecraft.world.item.Items.SHORT_GRASS ||
               item == net.minecraft.world.item.Items.TALL_GRASS ||
               item == net.minecraft.world.item.Items.OAK_SAPLING ||
               item == net.minecraft.world.item.Items.SPRUCE_SAPLING ||
               item == net.minecraft.world.item.Items.BIRCH_SAPLING ||
               item == net.minecraft.world.item.Items.JUNGLE_SAPLING ||
               item == net.minecraft.world.item.Items.PEONY ||
               item == net.minecraft.world.item.Items.ACACIA_SAPLING ||
               item == net.minecraft.world.item.Items.DARK_OAK_SAPLING ||
               item == net.minecraft.world.item.Items.SUNFLOWER ||
               item == net.minecraft.world.item.Items.REPEATER ||
               item == net.minecraft.world.item.Items.FERN ||
               item == net.minecraft.world.item.Items.NETHER_WART ||
               item == net.minecraft.world.item.Items.LILAC ||
               item == net.minecraft.world.item.Items.RED_MUSHROOM ||
               item == net.minecraft.world.item.Items.BROWN_MUSHROOM ||
               item == net.minecraft.world.item.Items.SUGAR_CANE;
    }
}
