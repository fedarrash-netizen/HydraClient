package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.Mth;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.utils.math.MathUtil;
import win.winlocker.utils.player.InventoryUtil;

public class ElytraTarget extends Module {
    
    private final SliderSetting attackRange = new SliderSetting("Дистанция", 25, 10, 50);
    private final BooleanSetting autoAttack = new BooleanSetting("Автоатака", true);
    private final SliderSetting attackSpeed = new SliderSetting("Скорость атаки", 250, 100, 500);
    private final BooleanSetting criticals = new BooleanSetting("Криты", true);
    private final BooleanSetting avoidWater = new BooleanSetting("Избегать воды", true);
    
    private LivingEntity target;
    private long lastAttackTime = 0;
    private double prevPosY;
    private boolean canCritical;
    private float currentYaw, currentPitch;
    
    public ElytraTarget() {
        super("ElytraTarget", Module.Category.COMBAT);
        
        addSetting(attackRange);
        addSetting(autoAttack);
        addSetting(attackSpeed);
        addSetting(criticals);
        addSetting(avoidWater);
    }
    
    @Override
    public void onEnable() {
        target = null;
        lastAttackTime = 0;
        prevPosY = 0;
        canCritical = false;
        
        if (Minecraft.getInstance().player != null) {
            currentYaw = Minecraft.getInstance().player.getYRot();
            currentPitch = Minecraft.getInstance().player.getXRot();
        }
    }
    
    @Override
    public void onDisable() {
        target = null;
    }
    
    @Override
    public void onTick() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        // Проверяем, на элитрах ли игрок
        if (!Minecraft.getInstance().player.isFallFlying() && !Minecraft.getInstance().player.getAbilities().flying) {
            return;
        }
        
        // Обновляем цель
        updateTarget();
        
        if (target == null) {
            return;
        }
        
        // Обновляем ротацию
        updateRotation();
        
        // Обновляем позицию для критов
        updateMotion();
        
        // Атакуем если нужно
        if (autoAttack.get() && shouldAttack()) {
            performAttack();
        }
    }
    
    private void updateTarget() {
        double range = attackRange.get();
        LivingEntity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        
        for (LivingEntity entity : Minecraft.getInstance().level.getEntitiesOfClass(LivingEntity.class, 
                Minecraft.getInstance().player.getBoundingBox().inflate(attackRange.get()))) {
            if (!isValidTarget(entity)) {
                continue;
            }
            
            double distance = Minecraft.getInstance().player.distanceToSqr(entity);
            if (distance < bestDistance && distance <= range * range) {
                bestDistance = distance;
                bestTarget = entity;
            }
        }
        
        target = bestTarget;
    }
    
    private boolean isValidTarget(LivingEntity entity) {
        if (entity == Minecraft.getInstance().player) {
            return false;
        }
        
        if (!entity.isAlive()) {
            return false;
        }
        
        if (entity.isRemoved()) {
            return false;
        }
        
        // Проверка на невидимость
        if (entity.isInvisible()) {
            return false;
        }
        
        // Проверка на игроков
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            // Проверка друзей
            if (win.winlocker.utils.friends.FriendStorage.getInstance().isFriend(player.getUUID())) {
                return false;
            }
        }
        
        return true;
    }
    
    private void updateRotation() {
        if (target == null || Minecraft.getInstance().player == null) {
            return;
        }
        
        // Вычисляем вектор к цели
        Vec3 playerEye = Minecraft.getInstance().player.getEyePosition();
        Vec3 targetPos = target.getEyePosition();
        Vec3 vec = targetPos.subtract(playerEye).normalize();
        
        // Вычисляем углы
        float rawYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float rawPitch = (float) Mth.clamp(Math.toDegrees(Math.asin(-vec.y)), -90, 90);
        
        // Вычисляем дельты
        float yawDelta = Mth.wrapDegrees(rawYaw - currentYaw);
        float pitchDelta = rawPitch - currentPitch;
        
        // Ограничиваем скорость ротации
        float yawSpeed = 15f; // Максимальная скорость поворота
        float pitchSpeed = 15f;
        
        float clampedYaw = Mth.clamp(yawDelta, -yawSpeed, yawSpeed);
        float clampedPitch = Mth.clamp(pitchDelta, -pitchSpeed, pitchSpeed);
        
        // Применяем ротацию
        currentYaw += clampedYaw;
        currentPitch += clampedPitch;
        
        // Нормализуем углы
        currentYaw = Mth.wrapDegrees(currentYaw);
        currentPitch = Mth.clamp(currentPitch, -90, 90);
        
        // Применяем к игроку
        Minecraft.getInstance().player.setYRot(currentYaw);
        Minecraft.getInstance().player.setXRot(currentPitch);
        Minecraft.getInstance().player.setYHeadRot(currentYaw);
    }
    
    private void updateMotion() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        
        double posY = Minecraft.getInstance().player.getY();
        canCritical = !Minecraft.getInstance().player.onGround() && posY < prevPosY;
        prevPosY = posY;
    }
    
    private boolean shouldAttack() {
        if (target == null || Minecraft.getInstance().player == null) {
            return false;
        }
        
        // Проверка задержки атаки
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < attackSpeed.get()) {
            return false;
        }
        
        // Проверка силы атаки - убираем так как в 1.21.4 нет этого метода
        // float attackStrength = Minecraft.getInstance().player.getAttackStrength(1.5f);
        // if (attackStrength < 1.0f) {
        //     return false;
        // }
        
        // Проверка на дебаффы
        if (isDebuffed()) {
            return false;
        }
        
        // Проверка дистанции
        double distance = Minecraft.getInstance().player.distanceTo(target);
        if (distance > attackRange.get()) {
            return false;
        }
        
        return true;
    }
    
    private boolean isDebuffed() {
        if (Minecraft.getInstance().player == null) {
            return true;
        }
        
        return Minecraft.getInstance().player.hasEffect(MobEffects.LEVITATION) ||
               Minecraft.getInstance().player.hasEffect(MobEffects.BLINDNESS) ||
               Minecraft.getInstance().player.hasEffect(MobEffects.SLOW_FALLING) ||
               (avoidWater.get() && Minecraft.getInstance().player.isEyeInFluid(FluidTags.WATER)) ||
               (avoidWater.get() && Minecraft.getInstance().player.isEyeInFluid(FluidTags.LAVA)) ||
               Minecraft.getInstance().player.getAbilities().flying ||
               Minecraft.getInstance().player.isFallFlying() ||
               Minecraft.getInstance().player.onClimbable() ||
               Minecraft.getInstance().player.isPassenger();
    }
    
    private void performAttack() {
        if (target == null || Minecraft.getInstance().player == null || 
            Minecraft.getInstance().gameMode == null) {
            return;
        }
        
        // Переключаемся на лучшее оружие если нужно
        int bestWeaponSlot = InventoryUtil.getBestWeaponSlot();
        if (bestWeaponSlot != -1) {
            Minecraft.getInstance().player.getInventory().selected = bestWeaponSlot;
        }
        
        // Выполняем атаку
        Minecraft.getInstance().gameMode.attack(Minecraft.getInstance().player, target);
        Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
        
        // Обновляем время атаки
        lastAttackTime = System.currentTimeMillis();
    }
    
    public LivingEntity getTarget() {
        return target;
    }
    
    public boolean hasTarget() {
        return target != null && target.isAlive();
    }
    
    public boolean canCritical() {
        return canCritical && criticals.get();
    }
}
