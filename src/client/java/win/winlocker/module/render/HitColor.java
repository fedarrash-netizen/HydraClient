package win.winlocker.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.combat.Aura;

public class HitColor extends Module {
    private static HitColor instance;

    private final SliderSetting red = new SliderSetting("Red", 255, 0, 255);
    private final SliderSetting green = new SliderSetting("Green", 0, 0, 255);
    private final SliderSetting blue = new SliderSetting("Blue", 0, 0, 255);
    private final SliderSetting alpha = new SliderSetting("Alpha", 200, 0, 255);

    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
    private final BooleanSetting targetOnly = new BooleanSetting("Target Only", true);
    private final BooleanSetting ignoreSelf = new BooleanSetting("Ignore Self", true);

    public HitColor() {
        super("HitColor", Category.RENDER);
        instance = this;

        addSetting(red);
        addSetting(green);
        addSetting(blue);
        addSetting(alpha);

        addSetting(players);
        addSetting(mobs);
        addSetting(targetOnly);
        addSetting(ignoreSelf);
    }

    public static HitColor getInstance() {
        return instance;
    }

    public int getTintColor() {
        int a = alpha.getInt() & 0xFF;
        int r = red.getInt() & 0xFF;
        int g = green.getInt() & 0xFF;
        int b = blue.getInt() & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public boolean shouldTint(LivingEntity entity, boolean hasRedOverlay) {
        if (!isEnabled() || entity == null || !hasRedOverlay) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (ignoreSelf.get() && mc.player != null && entity == mc.player) {
            return false;
        }

        if (entity instanceof Player) {
            if (!players.get()) {
                return false;
            }
        } else if (entity instanceof Mob) {
            if (!mobs.get()) {
                return false;
            }
        } else {
            return false;
        }

        if (!targetOnly.get()) {
            return true;
        }

        return isCurrentTarget(mc, entity);
    }

    private boolean isCurrentTarget(Minecraft mc, LivingEntity entity) {
        Aura aura = (Aura) ModuleManager.getModule(Aura.class);
        if (aura != null && aura.isEnabled() && aura.getCombatTarget() == entity) {
            return true;
        }

        HitResult hitResult = mc.hitResult;
        return hitResult instanceof EntityHitResult entityHitResult
                && entityHitResult.getEntity() == entity;
    }
}
