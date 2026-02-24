package win.winlocker.module.render;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.module.Module;

public class NoRender extends Module {
    public static final BooleanSetting FIRE = new BooleanSetting("Fire", true);
    public static final BooleanSetting BOSSBAR = new BooleanSetting("BossBar", true);
    public static final BooleanSetting WATER_LAVA = new BooleanSetting("Water/Lava Overlay", false);
    public static final BooleanSetting PLAYERS = new BooleanSetting("Players", false);
    public static final BooleanSetting GRASS = new BooleanSetting("Grass", false);
    public static final BooleanSetting CLOUDS = new BooleanSetting("Clouds", true);
    public static final BooleanSetting SKY = new BooleanSetting("Custom Sky", false);
    public static final BooleanSetting EFFECTS = new BooleanSetting("Status Effects", false);
    public static final BooleanSetting PARTICLES = new BooleanSetting("Particles", false);

    public NoRender() {
        super("NoRender", Category.RENDER);
        addSetting(FIRE);
        addSetting(BOSSBAR);
        addSetting(WATER_LAVA);
        addSetting(PLAYERS);
        addSetting(GRASS);
        addSetting(CLOUDS);
        addSetting(SKY);
        addSetting(EFFECTS);
        addSetting(PARTICLES);
    }
}
