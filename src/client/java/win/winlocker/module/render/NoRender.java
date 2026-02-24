package win.winlocker.module.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.module.Module;

public class NoRender extends Module {
    private static NoRender instance;

    // Settings from the provided NoRender implementation.
    private final BooleanSetting noFire = new BooleanSetting("No Fire", true);
    private final BooleanSetting noHurtCam = new BooleanSetting("No HurtCam", true);
    private final BooleanSetting noPumpkin = new BooleanSetting("No Pumpkin", true);
    private final BooleanSetting noPortal = new BooleanSetting("No Portal", true);
    private final BooleanSetting noTotem = new BooleanSetting("No Totem", true);
    private final BooleanSetting noWaterLava = new BooleanSetting("No Water/Lava", true);
    private final BooleanSetting noBlindness = new BooleanSetting("No Blindness", true);
    private final BooleanSetting noNausea = new BooleanSetting("No Nausea", true);

    // Existing options used by current mixins/features.
    private final BooleanSetting bossBar = new BooleanSetting("BossBar", true);
    private final BooleanSetting players = new BooleanSetting("Players", false);
    private final BooleanSetting grass = new BooleanSetting("Grass", false);
    private final BooleanSetting clouds = new BooleanSetting("Clouds", true);
    private final BooleanSetting customSky = new BooleanSetting("Custom Sky", false);
    private final BooleanSetting statusEffects = new BooleanSetting("Status Effects", false);
    private final BooleanSetting particles = new BooleanSetting("Particles", false);

    public NoRender() {
        super("NoRender", Category.RENDER);
        instance = this;

        addSetting(noFire);
        addSetting(noHurtCam);
        addSetting(noPumpkin);
        addSetting(noPortal);
        addSetting(noTotem);
        addSetting(noWaterLava);
        addSetting(noBlindness);
        addSetting(noNausea);

        addSetting(bossBar);
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

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (!isEnabled() || mc.player == null) {
            return;
        }

        if (noNausea.get() && mc.player.hasEffect(MobEffects.CONFUSION)) {
            mc.player.removeEffect(MobEffects.CONFUSION);
        }
        if (noBlindness.get() && mc.player.hasEffect(MobEffects.BLINDNESS)) {
            mc.player.removeEffect(MobEffects.BLINDNESS);
        }
        if (noFire.get() && mc.player.isOnFire()) {
            mc.player.clearFire();
        }
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
        if (noFire.get() && lower.contains("fire")) {
            return true;
        }
        if (noPumpkin.get() && lower.contains("pumpkin")) {
            return true;
        }
        if (noPortal.get() && lower.contains("portal")) {
            return true;
        }

        return noWaterLava.get() && (lower.contains("water") || lower.contains("lava") || lower.contains("powder_snow"));
    }

    public boolean shouldHidePortalOverlay() {
        return isEnabled() && noPortal.get();
    }

    public boolean shouldHideNauseaOverlay() {
        return isEnabled() && noNausea.get();
    }

    public boolean shouldHideWaterLavaOverlay() {
        return isEnabled() && noWaterLava.get();
    }

    public boolean shouldHideFireOverlay() {
        return isEnabled() && noFire.get();
    }

    public boolean shouldHideBlockOverlay(ResourceLocation textureId) {
        if (!isEnabled() || textureId == null) {
            return false;
        }

        String path = textureId.getPath();
        if (path == null) {
            return false;
        }

        String lower = path.toLowerCase();
        if (noPumpkin.get() && lower.contains("pumpkin")) {
            return true;
        }

        return noWaterLava.get() && (lower.contains("lava") || lower.contains("water") || lower.contains("powder_snow"));
    }

    public boolean shouldHideHurtCam() {
        return isEnabled() && noHurtCam.get();
    }

    public boolean shouldHideTotemAnimation(ItemStack activatingItem) {
        return isEnabled() && noTotem.get()
                && activatingItem != null
                && !activatingItem.isEmpty()
                && activatingItem.is(Items.TOTEM_OF_UNDYING);
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
        return isEnabled() && (statusEffects.get() || noBlindness.get() || noNausea.get());
    }
}
