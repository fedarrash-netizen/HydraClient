package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
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

    private final StopWatch timer = new StopWatch();
    private boolean wasOnGround;

    public Spider() {
        super("Spider", Category.MOVEMENT);

        this.mode = new ModeSetting("Mode", "FunTime", List.of("FunTime", "Vanilla", "NCP"));
        this.boost = new SliderSetting("Boost", 0.42, 0.1, 1.0);
        this.delay = new SliderSetting("Delay", 400, 100, 1000);
        this.onlyOnCollision = new BooleanSetting("Only Collision", true);
        this.debug = new BooleanSetting("Debug", false);

        addSetting(mode);
        addSetting(boost);
        addSetting(delay);
        addSetting(onlyOnCollision);
        addSetting(debug);
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
}
