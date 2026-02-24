package win.winlocker.module.render;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.Arrays;
import java.util.List;

public class FullBright extends Module {
    public final BooleanSetting enabled = new BooleanSetting("Enabled", false);
    public final ModeSetting mode = new ModeSetting("Mode", "Gamma", Arrays.asList("Gamma", "Night Vision", "FullBright"));
    public final SliderSetting brightness = new SliderSetting("Brightness", 15.0, 1.0, 50.0);
    public final BooleanSetting fadeEffect = new BooleanSetting("Fade Effect", true);
    public final BooleanSetting applyToEntities = new BooleanSetting("Apply To Entities", false);
    
    private static FullBright instance;
    private double originalGamma;
    private boolean hadNightVision;
    
    public FullBright() {
        super("FullBright", Category.RENDER);
        addSetting(enabled);
        addSetting(mode);
        addSetting(brightness);
        addSetting(fadeEffect);
        addSetting(applyToEntities);
        
        instance = this;
    }
    
    public static FullBright getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        if (originalGamma == 0) {
            originalGamma = net.minecraft.client.Minecraft.getInstance().options.gamma().get().doubleValue();
        }
        hadNightVision = net.minecraft.client.Minecraft.getInstance().player != null && 
                        net.minecraft.client.Minecraft.getInstance().player.hasEffect(net.minecraft.world.effect.MobEffects.NIGHT_VISION);
    }
    
    @Override
    public void onDisable() {
        // Восстанавливаем оригинальные настройки
        if (net.minecraft.client.Minecraft.getInstance().options != null) {
            net.minecraft.client.Minecraft.getInstance().options.gamma().set(originalGamma);
        }
    }
    
    @Override
    public void onTick() {
        if (!enabled.get()) return;
        
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.options == null) return;
        
        String currentMode = mode.get();
        
        switch (currentMode) {
            case "Gamma":
                applyGammaBrightness();
                break;
            case "Night Vision":
                applyNightVision();
                break;
            case "FullBright":
                applyFullBright();
                break;
        }
    }
    
    private void applyGammaBrightness() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        double targetBrightness = brightness.get();
        
        if (fadeEffect.get()) {
            // Плавное изменение яркости
            double currentGamma = mc.options.gamma().get().doubleValue();
            double newGamma = currentGamma + (targetBrightness - currentGamma) * 0.1;
            mc.options.gamma().set(newGamma);
        } else {
            // Мгновенное изменение
            mc.options.gamma().set(targetBrightness);
        }
    }
    
    private void applyNightVision() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            // Устанавливаем максимальную яркость как при ночном зрении
            mc.options.gamma().set(15.0);
        }
    }
    
    private void applyFullBright() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        // Максимальная яркость
        mc.options.gamma().set(brightness.get());
    }
    
    public float getModifiedGamma(float original) {
        if (!enabled.get()) return original;
        
        String currentMode = mode.get();
        
        switch (currentMode) {
            case "Gamma":
                return (float) brightness.get();
            case "Night Vision":
                return Math.max(original, 15.0f);
            case "FullBright":
                return (float) brightness.get();
            default:
                return original;
        }
    }
    
    public boolean shouldApplyToEntities() {
        return enabled.get() && applyToEntities.get();
    }
    
    public String getCurrentMode() {
        return mode.get();
    }
    
    public double getBrightness() {
        return brightness.get();
    }
    
    public boolean hasFadeEffect() {
        return fadeEffect.get();
    }
    
    // Для использования в рендере
    public float getLightLevelModifier() {
        if (!enabled.get()) return 1.0f;
        
        String currentMode = mode.get();
        
        switch (currentMode) {
            case "Gamma":
                return (float) (brightness.get() / 15.0);
            case "Night Vision":
                return 1.0f; // Ночное зрение дает полную яркость
            case "FullBright":
                return 1.0f;
            default:
                return 1.0f;
        }
    }
}
