package win.winlocker.module.render;

import net.minecraft.resources.ResourceLocation;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.module.Module;

public class NoRender extends Module {
    private static NoRender instance;

    private final BooleanSetting fireOverlay = new BooleanSetting("Fire Overlay", true);
    private final BooleanSetting bossBar = new BooleanSetting("BossBar", true);
    private final BooleanSetting waterLavaOverlay = new BooleanSetting("Water/Lava Overlay", false);
    private final BooleanSetting players = new BooleanSetting("Players", false);
    private final BooleanSetting grass = new BooleanSetting("Grass", false);
    private final BooleanSetting clouds = new BooleanSetting("Clouds", true);
    private final BooleanSetting customSky = new BooleanSetting("Custom Sky", false);
    private final BooleanSetting statusEffects = new BooleanSetting("Status Effects", false);
    private final BooleanSetting particles = new BooleanSetting("Particles", false);

    public NoRender() {
        super("NoRender", Category.RENDER);
        instance = this;

        addSetting(fireOverlay);
        addSetting(bossBar);
        addSetting(waterLavaOverlay);
        addSetting(players);
        addSetting(grass);
        addSetting(clouds);
        addSetting(customSky);
        addSetting(statusEffects);
        addSetting(particles);
    }

    public static NoRender getInstance() {
        return instance;
    }

    public boolean shouldCancelOverlay(ResourceLocation resourceLocation) {
        if (!isEnabled() || resourceLocation == null) {
            return false;
        }

        String path = resourceLocation.getPath();
        if (path == null) {
            return false;
        }

        String lower = path.toLowerCase();
        if (fireOverlay.get() && lower.contains("fire")) {
            return true;
        }

        return waterLavaOverlay.get()
                && (lower.contains("water") || lower.contains("lava") || lower.contains("powder_snow"));
    }

    public boolean shouldHideBossBar() {
        return isEnabled() && bossBar.get();
    }

    public boolean shouldBlockParticles() {
        return isEnabled() && particles.get();
    }

    public boolean shouldHidePlayers() {
        return isEnabled() && players.get();
    }

    public boolean shouldHideGrass() {
        return isEnabled() && grass.get();
    }

    public boolean shouldHideClouds() {
        return isEnabled() && clouds.get();
    }

    public boolean shouldHideCustomSky() {
        return isEnabled() && customSky.get();
    }

    public boolean shouldHideStatusEffects() {
        return isEnabled() && statusEffects.get();
    }
}
