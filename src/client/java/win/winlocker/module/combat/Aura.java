package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.friends.FriendStorage;

import java.util.List;

public class Aura extends Module {
    private final ModeSetting sortMode = new ModeSetting("Sort", "Closest", List.of("Closest", "Lowest HP"));
    private final ModeSetting serverMode = new ModeSetting("Server", "None", List.of("None", "HolyWorld", "FunTime"));
    private final SliderSetting range = new SliderSetting("Range", 4.0, 1.0, 6.0);
    private final SliderSetting cps = new SliderSetting("CPS", 10.0, 1.0, 20.0);
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting ignoreFriends = new BooleanSetting("Ignore Friends", true);
    private final BooleanSetting ignoreInvisible = new BooleanSetting("Ignore Invisible", true);
    private final BooleanSetting onlyCriticals = new BooleanSetting("Only Criticals", false);
    private final SliderSetting criticalsSpeed = new SliderSetting("Criticals Speed", 1.0, 0.5, 3.0);
    private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", false);

    private final FriendStorage friendStorage = FriendStorage.getInstance();

    private int attackDelayTicks = 0;
    private LivingEntity target;

    public Aura() {
        super("Aura", Category.COMBAT);
        addSetting(sortMode);
        addSetting(serverMode);
        addSetting(range);
        addSetting(cps);
        addSetting(targetPlayers);
        addSetting(targetMobs);
        addSetting(ignoreFriends);
        addSetting(ignoreInvisible);
        addSetting(onlyCriticals);
        addSetting(criticalsSpeed);
        addSetting(throughWalls);
    }

    @Override
    public void onDisable() {
        target = null;
        attackDelayTicks = 0;
        resetServerBypass();
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            target = null;
            return;
        }

        applyServerBypass();

        if (attackDelayTicks > 0) {
            attackDelayTicks--;
        }

        target = findTarget(mc);
        if (target == null) {
            return;
        }

        if (attackDelayTicks > 0) {
            return;
        }

        if (onlyCriticals.get() && !isCriticalState(mc)) {
            return;
        }

        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
        int baseDelay = Math.max(1, (int) Math.round(20.0 / cps.get()));
        attackDelayTicks = onlyCriticals.get()
                ? (int) Math.round(baseDelay / criticalsSpeed.get())
                : baseDelay;
    }

    public Player getTarget() {
        return target instanceof Player ? (Player) target : null;
    }

    public LivingEntity getCombatTarget() {
        return target;
    }

    private void applyServerBypass() {
        String server = serverMode.get();
        
        if ("HolyWorld".equals(server)) {
            // HolyWorld - Grim 2.3.72 + Sloth AI обход
            range.set(3.5);
            cps.set(8.0);
            throughWalls.set(false);
        } else if ("FunTime".equals(server)) {
            // FunTime - Polar античит обход
            range.set(3.0);
            cps.set(6.0);
            onlyCriticals.set(true);
            throughWalls.set(false);
        }
        // "None" - использует пользовательские настройки
    }

    private void resetServerBypass() {
        // Сброс настроек при отключении модуля
        // Настройки возвращаются к значениям по умолчанию через ModeSetting
    }

    private LivingEntity findTarget(Minecraft mc) {
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        float bestHealth = Float.MAX_VALUE;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            LivingEntity living = (LivingEntity) entity;
            if (!isValidTarget(mc, living)) {
                continue;
            }

            double distance = mc.player.distanceTo(living);
            if ("Lowest HP".equals(sortMode.get())) {
                float health = living.getHealth() + living.getAbsorptionAmount();
                if (health < bestHealth || (health == bestHealth && distance < bestDistance)) {
                    best = living;
                    bestHealth = health;
                    bestDistance = distance;
                }
                continue;
            }

            if (distance < bestDistance) {
                best = living;
                bestDistance = distance;
            }
        }

        return best;
    }

    private boolean isValidTarget(Minecraft mc, LivingEntity living) {
        if (living == mc.player || !living.isAlive() || living.isRemoved()) {
            return false;
        }
        if (mc.player.distanceTo(living) > range.get()) {
            return false;
        }
        if (ignoreInvisible.get() && living.isInvisible()) {
            return false;
        }
        if (!throughWalls.get() && !mc.player.hasLineOfSight(living)) {
            return false;
        }

        if (living instanceof Player) {
            if (!targetPlayers.get()) {
                return false;
            }
            if (ignoreFriends.get() && friendStorage.isFriend((Player) living)) {
                return false;
            }
            return true;
        }

        if (living instanceof Mob) {
            return targetMobs.get();
        }

        return false;
    }

    private boolean isCriticalState(Minecraft mc) {
        return !mc.player.onGround()
                && mc.player.fallDistance > 0.0F
                && !mc.player.isInWater()
                && !mc.player.onClimbable()
                && !mc.player.isPassenger();
    }
}
