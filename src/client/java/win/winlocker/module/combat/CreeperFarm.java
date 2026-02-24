package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ModeSetting;

import java.util.ArrayList;
import java.util.List;

public class CreeperFarm extends Module {
    
    // Основные настройки
    private final SliderSetting searchRadius = new SliderSetting("Радиус поиска", 8.0, 3.0, 20.0);
    private final SliderSetting attackRange = new SliderSetting("Дистанция атаки", 3.5, 1.0, 6.0);
    private final SliderSetting retreatDistance = new SliderSetting("Дистанция отхода", 6.0, 3.0, 10.0);
    
    // Настройки движения
    private final BooleanSetting avoidObstacles = new BooleanSetting("Обход препятствий", true);
    private final BooleanSetting autoJump = new BooleanSetting("Автопрыжки", true);
    private final BooleanSetting autoAttack = new BooleanSetting("Автоатака", true);
    private final BooleanSetting autoSword = new BooleanSetting("Автомеч", true);
    
    // Режимы движения
    private final ModeSetting movePattern = new ModeSetting("Движение", "Квадрат", 
            java.util.List.of("Квадрат", "Круг", "Вперед-назад", "Случайный"));
    
    // Переменные
    private Creeper targetCreeper;
    private Vec3 currentTarget;
    private int moveTimer = 0;
    private int attackCooldown = 0;
    private boolean isRetreating = false;
    private int retreatTimer = 0;
    private Vec3[] squarePattern;
    private int patternIndex = 0;
    
    public CreeperFarm() {
        super("CreeperFarm", Module.Category.COMBAT);
        
        addSetting(searchRadius);
        addSetting(attackRange);
        addSetting(retreatDistance);
        addSetting(avoidObstacles);
        addSetting(autoJump);
        addSetting(autoAttack);
        addSetting(autoSword);
        addSetting(movePattern);
    }
    
    @Override
    public void onEnable() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        targetCreeper = null;
        currentTarget = null;
        moveTimer = 0;
        attackCooldown = 0;
        isRetreating = false;
        retreatTimer = 0;
        patternIndex = 0;
        
        // Инициализация паттерна движения
        initializeMovementPattern();
    }
    
    @Override
    public void onDisable() {
        targetCreeper = null;
        currentTarget = null;
        stopMovement();
    }
    
    @Override
    public void onTick() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        
        // Поиск криперов
        findCreeper();
        
        // Если есть крипер - атакуем его
        if (targetCreeper != null) {
            handleCreeperCombat(player);
        } else {
            // Движение по паттерну
            handleMovement(player);
        }
        
        // Обновление таймеров
        updateTimers();
    }
    
    private void initializeMovementPattern() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        
        Vec3 center = player.position();
        double size = 5.0;
        
        // Квадратный паттерн
        squarePattern = new Vec3[] {
            new Vec3(center.x + size, center.y, center.z),      // Право
            new Vec3(center.x + size, center.y, center.z + size), // Право-вперед
            new Vec3(center.x, center.y, center.z + size),       // Вперед
            new Vec3(center.x - size, center.y, center.z + size), // Лево-вперед
            new Vec3(center.x - size, center.y, center.z),       // Лево
            new Vec3(center.x - size, center.y, center.z - size), // Лево-назад
            new Vec3(center.x, center.y, center.z - size),       // Назад
            new Vec3(center.x + size, center.y, center.z - size)  // Право-назад
        };
    }
    
    private void findCreeper() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        List<Creeper> creepers = new ArrayList<>();
        
        // Поиск криперов в радиусе
        for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
            if (entity instanceof Creeper creeper && creeper.isAlive()) {
                double distance = player.distanceToSqr(creeper);
                double maxDistance = searchRadius.get() * searchRadius.get();
                
                if (distance <= maxDistance && hasLineOfSight(player, creeper)) {
                    creepers.add(creeper);
                }
            }
        }
        
        // Выбираем ближайшего крипера
        if (!creepers.isEmpty()) {
            targetCreeper = creepers.get(0);
            for (Creeper creeper : creepers) {
                if (player.distanceToSqr(creeper) < player.distanceToSqr(targetCreeper)) {
                    targetCreeper = creeper;
                }
            }
        } else {
            targetCreeper = null;
        }
    }
    
    private boolean hasLineOfSight(Player player, Entity target) {
        Vec3 playerEye = player.getEyePosition();
        Vec3 targetEye = target.getEyePosition();
        
        ClipContext context = new ClipContext(playerEye, targetEye, 
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        
        HitResult result = Minecraft.getInstance().level.clip(context);
        
        return result.getType() == HitResult.Type.ENTITY || 
               result.getType() == HitResult.Type.MISS;
    }
    
    private void handleCreeperCombat(Player player) {
        if (targetCreeper == null || !targetCreeper.isAlive()) {
            targetCreeper = null;
            return;
        }
        
        double distance = player.distanceTo(targetCreeper);
        
        // Проверяем взрыв крипера
        if (targetCreeper.isPowered() || targetCreeper.getSwellDir() > 0) {
            // Крипер взрывается - отходим
            retreatFromCreeper(player);
        } else if (distance <= attackRange.get()) {
            // В дистанции атаки
            if (!isRetreating) {
                attackCreeper(player);
            }
        } else {
            // Бежим к криперу
            moveToCreeper(player);
        }
        
        // Переключаемся на меч
        if (autoSword.get()) {
            switchToSword();
        }
    }
    
    private void retreatFromCreeper(Player player) {
        if (!isRetreating) {
            isRetreating = true;
            retreatTimer = 20; // Отходим 1 секунду
        }
        
        // Отходим от крипера
        Vec3 awayFromCreeper = player.position().subtract(targetCreeper.position()).normalize();
        Vec3 retreatPos = player.position().add(awayFromCreeper.scale(retreatDistance.get()));
        
        // Двигаемся к точке отхода
        moveToPosition(player, retreatPos);
        
        retreatTimer--;
        if (retreatTimer <= 0) {
            isRetreating = false;
        }
    }
    
    private void moveToCreeper(Player player) {
        if (targetCreeper == null) return;
        
        Vec3 targetPos = targetCreeper.position();
        moveToPosition(player, targetPos);
    }
    
    private void attackCreeper(Player player) {
        if (!autoAttack.get() || attackCooldown > 0) {
            return;
        }
        
        // Атакуем крипера
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.attack(player, targetCreeper);
            player.swing(InteractionHand.MAIN_HAND);
            attackCooldown = 10; // 0.5 секунды между атаками
        }
    }
    
    private void handleMovement(Player player) {
        if (currentTarget == null || moveTimer <= 0) {
            // Выбираем новую цель движения
            selectMovementTarget(player);
            moveTimer = 60; // 3 секунды на одну точку
        }
        
        // Двигаемся к цели
        if (currentTarget != null) {
            moveToPosition(player, currentTarget);
            
            // Проверяем достигли ли цели
            if (player.distanceToSqr(currentTarget) < 1.0) {
                moveTimer = 0;
            }
        }
    }
    
    private void selectMovementTarget(Player player) {
        switch (movePattern.get()) {
            case "Квадрат":
                currentTarget = squarePattern[patternIndex];
                patternIndex = (patternIndex + 1) % squarePattern.length;
                break;
                
            case "Круг":
                currentTarget = getCircularTarget(player);
                break;
                
            case "Вперед-назад":
                currentTarget = getForwardBackwardTarget(player);
                break;
                
            case "Случайный":
                currentTarget = getRandomTarget(player);
                break;
        }
    }
    
    private Vec3 getCircularTarget(Player player) {
        double radius = 5.0;
        double angle = (moveTimer * 0.1) % (2 * Math.PI);
        
        double x = player.getX() + Math.cos(angle) * radius;
        double z = player.getZ() + Math.sin(angle) * radius;
        
        return new Vec3(x, player.getY(), z);
    }
    
    private Vec3 getForwardBackwardTarget(Player player) {
        Vec3 look = player.getLookAngle();
        double distance = moveTimer % 40 < 20 ? 5.0 : -5.0;
        
        return player.position().add(look.scale(distance));
    }
    
    private Vec3 getRandomTarget(Player player) {
        double radius = 8.0;
        double angle = Math.random() * 2 * Math.PI;
        
        double x = player.getX() + Math.cos(angle) * radius;
        double z = player.getZ() + Math.sin(angle) * radius;
        
        return new Vec3(x, player.getY(), z);
    }
    
    private void moveToPosition(Player player, Vec3 target) {
        Vec3 direction = target.subtract(player.position()).normalize();
        
        // Обход препятствий
        if (avoidObstacles.get()) {
            direction = avoidObstacles(player, direction);
        }
        
        // Применяем движение как обычный игрок
        Vec3 currentMotion = player.getDeltaMovement();
        player.setDeltaMovement(
            direction.x * 0.1 + currentMotion.x * 0.9,
            currentMotion.y,
            direction.z * 0.1 + currentMotion.z * 0.9
        );
        
        // Автопрыжки
        if (autoJump.get() && shouldJump(player)) {
            player.jumpFromGround();
        }
        
        // Смотрим в направлении движения
        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        player.setYRot(yaw);
    }
    
    private Vec3 avoidObstacles(Player player, Vec3 direction) {
        // Проверяем препятствия впереди
        Vec3 nextPos = player.position().add(direction.scale(1.0));
        BlockPos blockPos = new BlockPos((int) nextPos.x, (int) nextPos.y, (int) nextPos.z);
        
        if (!Minecraft.getInstance().level.getBlockState(blockPos).isAir()) {
            // Препятствие - пытаемся обойти
            // Поворачиваем на 45 градусов
            double angle = Math.atan2(direction.z, direction.x) + Math.PI / 4;
            return new Vec3(Math.cos(angle), 0, Math.sin(angle)).normalize();
        }
        
        return direction;
    }
    
    private boolean shouldJump(Player player) {
        // Прыгаем если есть блок впереди
        Vec3 nextPos = player.position().add(player.getLookAngle().scale(1.0));
        BlockPos blockPos = new BlockPos((int) nextPos.x, (int) nextPos.y + 1, (int) nextPos.z);
        
        return !Minecraft.getInstance().level.getBlockState(blockPos).isAir() && player.onGround();
    }
    
    private void switchToSword() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        
        // Ищем меч в горячих слотах
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof SwordItem) {
                player.getInventory().selected = i;
                break;
            }
        }
    }
    
    private void stopMovement() {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.setDeltaMovement(Vec3.ZERO);
        }
    }
    
    private void updateTimers() {
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (moveTimer > 0) {
            moveTimer--;
        }
    }
    
    public String getDisplayInfo() {
        if (targetCreeper != null) {
            return "Цель: " + (int) Minecraft.getInstance().player.distanceTo(targetCreeper) + "м";
        }
        return movePattern.get();
    }
}
