package win.winlocker.module.render;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.DropDown.settings.SliderSetting;

/**
 * Модуль для ESP (Extra Sensory Perception)
 */
public class ESP extends Module {
    private final BooleanSetting showBox;
    private final BooleanSetting showGlow;
    private final BooleanSetting showName;
    private final BooleanSetting showHealth;
    private final BooleanSetting showDistance;
    private final ColorSetting boxColor;
    private final ColorSetting glowColor;
    private final SliderSetting range;

    public ESP() {
        super("ESP", Category.RENDER);

        this.showBox = new BooleanSetting("Show Box", true);
        this.showGlow = new BooleanSetting("Show Glow", false);
        this.showName = new BooleanSetting("Show Name", true);
        this.showHealth = new BooleanSetting("Show Health", true);
        this.showDistance = new BooleanSetting("Show Distance", false);
        this.boxColor = new ColorSetting("Box Color", 0xFF00FF00);
        this.glowColor = new ColorSetting("Glow Color", 0x4000FF00);
        this.range = new SliderSetting("Range", 100f, 10f, 500f);

        addSetting(showBox);
        addSetting(showGlow);
        addSetting(showName);
        addSetting(showHealth);
        addSetting(showDistance);
        addSetting(boxColor);
        addSetting(glowColor);
        addSetting(range);
    }

    public boolean shouldShowBox() {
        return showBox.get();
    }

    public boolean shouldShowGlow() {
        return showGlow.get();
    }

    public boolean shouldShowName() {
        return showName.get();
    }

    public boolean shouldShowHealth() {
        return showHealth.get();
    }

    public boolean shouldShowDistance() {
        return showDistance.get();
    }

    public int getBoxColor() {
        return boxColor.get();
    }

    public int getGlowColor() {
        return glowColor.get();
    }

    public float getRange() {
        return (float) range.get();
    }
}
