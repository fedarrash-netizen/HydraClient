package win.winlocker.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import win.winlocker.ClientSettings;

public class BotLogic {
    private static int attackDelay = 0;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (ClientSettings.TARGETING_ENABLED.get()) {
            handleTargeting(mc);
        }

        if (ClientSettings.BOTAIM_ENABLED.get()) {
            handleBotAim(mc);
        }
    }

    private static void handleTargeting(Minecraft mc) {
        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

        HitResult hr = mc.hitResult;
        if (hr instanceof EntityHitResult) {
            Entity target = ((EntityHitResult) hr).getEntity();
            if (target instanceof Player && target.isAlive()) {
                mc.gameMode.attack(mc.player, target);
                mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                attackDelay = 10; // Simple delay
            }
        }
    }

    private static void handleBotAim(Minecraft mc) {
        // Find nearest player
        Player target = null;
        double dist = ClientSettings.TARGETING_RANGE.get();
        
        for (Player p : mc.level.players()) {
            if (p == mc.player || !p.isAlive()) continue;
            double d = mc.player.distanceTo(p);
            if (d < dist) {
                dist = d;
                target = p;
            }
        }

        if (target != null) {
            // Very simple visual aim assist (smooth rotation)
            double dx = target.getX() - mc.player.getX();
            double dy = (target.getY() + target.getEyeHeight()) - (mc.player.getY() + mc.player.getEyeHeight());
            double dz = target.getZ() - mc.player.getZ();
            double dh = Math.sqrt(dx * dx + dz * dz);

            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));

            float speed = 0.1f; // Slow/legit speed
            mc.player.setYRot(mc.player.getYRot() + (yaw - mc.player.getYRot()) * speed);
            mc.player.setXRot(mc.player.getXRot() + (pitch - mc.player.getXRot()) * speed);
        }
    }
}
