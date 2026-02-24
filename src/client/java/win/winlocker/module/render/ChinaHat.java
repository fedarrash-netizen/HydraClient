package win.winlocker.module.render;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.Arrays;
import java.util.List;

public class ChinaHat extends Module {
    public final BooleanSetting enabled = new BooleanSetting("Enabled", false);
    public final SliderSetting hatHeight = new SliderSetting("Hat Height", 0.8, 0.3, 2.0);
    public final SliderSetting hatRadius = new SliderSetting("Hat Radius", 0.6, 0.3, 1.5);
    public final SliderSetting hatYOffset = new SliderSetting("Y Offset", 0.1, -0.5, 1.0);
    public final ModeSetting colorMode = new ModeSetting("Color Mode", "Solid", Arrays.asList("Solid", "Rainbow", "Fade"));
    public final SliderSetting red = new SliderSetting("Red", 255, 0, 255);
    public final SliderSetting green = new SliderSetting("Green", 50, 0, 255);
    public final SliderSetting blue = new SliderSetting("Blue", 50, 0, 255);
    public final SliderSetting alpha = new SliderSetting("Alpha", 150, 0, 255);
    public final BooleanSetting showOnlySelf = new BooleanSetting("Show Only Self", true);
    public final BooleanSetting showOnOthers = new BooleanSetting("Show On Others", false);
    
    private static ChinaHat instance;
    
    public ChinaHat() {
        super("ChinaHat", Category.RENDER);
        addSetting(enabled);
        addSetting(hatHeight);
        addSetting(hatRadius);
        addSetting(hatYOffset);
        addSetting(colorMode);
        addSetting(red);
        addSetting(green);
        addSetting(blue);
        addSetting(alpha);
        addSetting(showOnlySelf);
        addSetting(showOnOthers);
        
        instance = this;
    }
    
    public static ChinaHat getInstance() {
        return instance;
    }
    
    public int getColor() {
        String mode = colorMode.get();
        
        switch (mode) {
            case "Rainbow":
                long time = System.currentTimeMillis();
                int hue = (int) ((time / 10) % 360);
                return java.awt.Color.getHSBColor(hue / 360f, 1.0f, 1.0f).getRGB();
            case "Fade":
                long fadeTime = System.currentTimeMillis();
                int fadeAlpha = (int) (127 + 127 * Math.sin(fadeTime / 500.0));
                return ((int)red.get() << 16) | ((int)green.get() << 8) | (int)blue.get() | (fadeAlpha << 24);
            default:
                return ((int)red.get() << 16) | ((int)green.get() << 8) | (int)blue.get() | ((int)alpha.get() << 24);
        }
    }
    
    public boolean shouldRenderOnSelf() {
        return enabled.get() && showOnlySelf.get();
    }
    
    public boolean shouldRenderOnOthers() {
        return enabled.get() && showOnOthers.get();
    }
    
    public double getHatHeight() {
        return hatHeight.get();
    }
    
    public double getHatRadius() {
        return hatRadius.get();
    }
    
    public double getHatYOffset() {
        return hatYOffset.get();
    }
}
