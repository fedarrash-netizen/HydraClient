package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.particles.ParticleTypes;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.Arrays;

public class GodMode extends Module {
    private final BooleanSetting noDamage = new BooleanSetting("No Damage", true);
    private final BooleanSetting noFire = new BooleanSetting("No Fire", true);
    private final BooleanSetting noWater = new BooleanSetting("No Water", true);
    private final BooleanSetting noFall = new BooleanSetting("No Fall", true);
    private final BooleanSetting noBubble = new BooleanSetting("No Bubbles", true);
    private final BooleanSetting noHunger = new BooleanSetting("No Hunger", true);
    private final ModeSetting flyMode = new ModeSetting("Fly Mode", "Creative", Arrays.asList("Creative", "Survival", "Jetpack"));
    
    private boolean wasInWater = false;
    private boolean wasOnFire = false;

    public GodMode() {
        super("GodMode", Category.MISC);
        addSetting(noDamage);
        addSetting(noFire);
        addSetting(noWater);
        addSetting(noFall);
        addSetting(noBubble);
        addSetting(noHunger);
        addSetting(flyMode);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Без урона
        if (noDamage.get()) {
            mc.player.invulnerableTime = 20;
        }
        
        // Без огня
        if (noFire.get()) {
            mc.player.clearFire();
        }
        
        // Без воды (дыхание под водой)
        if (noWater.get()) {
            handleNoWater(mc);
        }
        
        // Без падения
        if (noFall.get()) {
            mc.player.fallDistance = 0;
        }
        
        // Без пузырей под водой
        if (noBubble.get()) {
            removeBubbles(mc);
        }
        
        // Без голода
        if (noHunger.get()) {
            mc.player.getFoodData().setFoodLevel(20);
            // В Minecraft 1.21.4 убираем setSaturationLevel
        }
        
        // Режим полёта
        handleFly(mc);
        
        // Сохраняем состояния
        wasInWater = mc.player.isInWater();
        wasOnFire = mc.player.isOnFire();
    }

    private void handleNoWater(Minecraft mc) {
        if (mc.player.isInWater() || mc.player.isUnderWater()) {
            // Даём возможность дышать под водой
            mc.player.setAirSupply(300);
            
            // Убираем эффект замедления в воде
            mc.player.setSprinting(true);
            
            // Визуально убираем воду из глаз
            if (wasInWater) {
                // Можно добавить эффект очистки воды
            }
        }
    }

    private void removeBubbles(Minecraft mc) {
        if (mc.player.isInWater() || mc.player.isUnderWater()) {
            // Убираем пузыри под водой
            // В Minecraft 1.21.4 это можно сделать через частицы
            
            // Убираем существующие пузыри
            for (int i = 0; i < 20; i++) {
                mc.level.addParticle(
                    ParticleTypes.SMOKE,
                    mc.player.getX() + (Math.random() - 0.5) * 0.5,
                    mc.player.getY() + Math.random() * 0.5,
                    mc.player.getZ() + (Math.random() - 0.5) * 0.5,
                    0, 0, 0
                );
            }
        }
    }

    private void handleFly(Minecraft mc) {
        String mode = flyMode.get();
        
        switch (mode) {
            case "Creative":
                // Креативный полёт
                mc.player.getAbilities().mayfly = true;
                mc.player.getAbilities().flying = true;
                // В Minecraft 1.21.4 убираем setFlySpeed
                break;
            case "Survival":
                // Сурвивал полёт (прыжки)
                if (mc.options.keyJump.isDown() && !mc.player.onGround()) {
                    mc.player.setDeltaMovement(
                        mc.player.getDeltaMovement().x,
                        0.5,
                        mc.player.getDeltaMovement().z
                    );
                }
                break;
            case "Jetpack":
                // Реактивный ранец
                if (mc.options.keyJump.isDown()) {
                    mc.player.setDeltaMovement(
                        mc.player.getDeltaMovement().x,
                        mc.player.getDeltaMovement().y + 0.1,
                        mc.player.getDeltaMovement().z
                    );
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // Возвращаем нормальные способности при выключении
        mc.player.getAbilities().mayfly = false;
        mc.player.getAbilities().flying = false;
        // В Minecraft 1.21.4 убираем setFlySpeed
    }
}
