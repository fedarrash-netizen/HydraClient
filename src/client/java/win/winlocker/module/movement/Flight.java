package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.BooleanSetting;

import java.util.List;

public class Flight extends Module {
    
    private final ModeSetting mode = new ModeSetting("Режим", "Levitation", 
            List.of("Vanilla", "Levitation", "Jetpack", "Grim 2.3.72"));
    
    private final SliderSetting speed = new SliderSetting("Скорость", 1.0, 0.1, 5.0);
    private final SliderSetting verticalSpeed = new SliderSetting("Вертикальная скорость", 1.0, 0.1, 3.0);
    private final SliderSetting levitationAmplifier = new SliderSetting("Уровень левитации", 1, 0, 5);
    private final BooleanSetting antiKick = new BooleanSetting("Анти-кик", true);
    private final BooleanSetting damage = new BooleanSetting("Урон при включении", false);
    
    private int levitationTimer = 0;
    private int antiKickTimer = 0;
    private boolean wasLevitating = false;
    
    public Flight() {
        super("Flight", Category.MOVEMENT);
        
        addSetting(mode);
        addSetting(speed);
        addSetting(verticalSpeed);
        addSetting(levitationAmplifier);
        addSetting(antiKick);
        addSetting(damage);
    }
    
    @Override
    public void onEnable() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        
        levitationTimer = 0;
        antiKickTimer = 0;
        wasLevitating = false;
        
        // Наносим урон если включено
        if (damage.get()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.hurt(player.damageSources().generic(), 1.0f);
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        
        // Убираем эффект левитации если был
        Player player = Minecraft.getInstance().player;
        if (player != null && wasLevitating) {
            player.removeEffect(MobEffects.LEVITATION);
            wasLevitating = false;
        }
        
        // Выключаем полет для других режимов
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().flying = false;
        }
    }
    
    @Override
    public void onTick() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        
        switch (mode.get()) {
            case "Vanilla":
                handleVanillaFlight(player);
                break;
            case "Levitation":
                handleLevitationFlight(player);
                break;
            case "Jetpack":
                handleJetpackFlight(player);
                break;
            case "Grim 2.3.72":
                handleGrimFlight(player);
                break;
        }
        
        // Анти-кик
        if (antiKick.get()) {
            handleAntiKick(player);
        }
    }
    
    private void handleVanillaFlight(Player player) {
        // Устанавливаем режим полета
        player.getAbilities().flying = true;
        player.getAbilities().setFlyingSpeed((float) speed.get() * 0.05f);
        
        // Управление вертикальной скоростью
        Vec3 motion = player.getDeltaMovement();
        float vertical = 0.0f;
        
        if (Minecraft.getInstance().options.keyJump.isDown()) {
            vertical = (float) verticalSpeed.get() * 0.1f;
        }
        if (Minecraft.getInstance().options.keyShift.isDown()) {
            vertical = -(float) verticalSpeed.get() * 0.1f;
        }
        
        player.setDeltaMovement(motion.x, vertical, motion.z);
    }
    
    private void handleLevitationFlight(Player player) {
        // Применяем эффект левитации как зелье
        int amplifier = levitationAmplifier.getInt();
        int duration = 200; // 10 секунд как стандартное зелье
        
        MobEffectInstance levitationEffect = new MobEffectInstance(
            MobEffects.LEVITATION, 
            duration, 
            amplifier, 
            false, 
            false
        );
        
        player.addEffect(levitationEffect);
        wasLevitating = true;
        
        // Таймер для обновления эффекта
        levitationTimer++;
        if (levitationTimer >= 150) { // Обновляем каждые 7.5 секунд
            levitationTimer = 0;
        }
        
        // Управление скоростью левитации через motion
        Vec3 motion = player.getDeltaMovement();
        float verticalSpeed = (float) this.verticalSpeed.get() * 0.05f;
        
        if (Minecraft.getInstance().options.keyJump.isDown()) {
            player.setDeltaMovement(motion.x, verticalSpeed, motion.z);
        } else if (Minecraft.getInstance().options.keyShift.isDown()) {
            player.setDeltaMovement(motion.x, -verticalSpeed * 0.5f, motion.z);
        }
        
        // Горизонтальное движение
        float horizontalSpeed = (float) speed.get() * 0.1f;
        if (Minecraft.getInstance().options.keyUp.isDown()) {
            Vec3 lookVec = player.getLookAngle();
            player.setDeltaMovement(
                lookVec.x * horizontalSpeed,
                player.getDeltaMovement().y,
                lookVec.z * horizontalSpeed
            );
        }
    }
    
    private void handleJetpackFlight(Player player) {
        // Режим джетпака - импульсный полет
        Vec3 motion = player.getDeltaMovement();
        float jetPower = (float) speed.get() * 0.15f;
        float verticalPower = (float) verticalSpeed.get() * 0.2f;
        
        if (Minecraft.getInstance().options.keyJump.isDown()) {
            player.setDeltaMovement(motion.x, verticalPower, motion.z);
        }
        if (Minecraft.getInstance().options.keyShift.isDown()) {
            player.setDeltaMovement(motion.x, -verticalPower * 0.7f, motion.z);
        }
        
        // Горизонтальное движение
        if (Minecraft.getInstance().options.keyUp.isDown()) {
            Vec3 lookVec = player.getLookAngle();
            player.setDeltaMovement(
                motion.x + lookVec.x * jetPower,
                motion.y,
                motion.z + lookVec.z * jetPower
            );
        }
    }
    
    private void handleGrimFlight(Player player) {
        // Старый режим для совместимости
        player.getAbilities().flying = true;
    }
    
    private void handleAntiKick(Player player) {
        antiKickTimer++;
        
        // Каждые 20 тиков (1 секунда) делаем небольшое падение
        if (antiKickTimer >= 20) {
            antiKickTimer = 0;
            
            // Небольшое падение для обхода анти-чита
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, -0.1, motion.z);
        }
    }
    
    public String getDisplayInfo() {
        return mode.get();
    }
}
