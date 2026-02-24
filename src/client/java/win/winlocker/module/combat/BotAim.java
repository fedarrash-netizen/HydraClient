package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

import java.util.List;

public class BotAim extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 2.3.72", List.of("Vanilla", "Grim 2.3.72"));
    private final SliderSetting range = new SliderSetting("Range", 4.0, 1.0, 10.0);
    private final SliderSetting speed = new SliderSetting("Speed", 0.1, 0.01, 1.0);

    public BotAim() {
        super("BotAim", Category.COMBAT);
        addSetting(mode);
        addSetting(range);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player target = null;
        double dist = range.get();
        
        for (Player p : mc.level.players()) {
            if (p == mc.player || !p.isAlive()) continue;
            double d = mc.player.distanceTo(p);
            if (d < dist) {
                dist = d;
                target = p;
            }
        }

        if (target != null) {
            double dx = target.getX() - mc.player.getX();
            double dy = (target.getY() + target.getEyeHeight()) - (mc.player.getY() + mc.player.getEyeHeight());
            double dz = target.getZ() - mc.player.getZ();
            double dh = Math.sqrt(dx * dx + dz * dz);

            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));

            float s = (float) speed.get();
            if (mode.get().equals("Grim 2.3.72")) {
                s *= 0.5f; // Even smoother for Grim
            }
            mc.player.setYRot(mc.player.getYRot() + (yaw - mc.player.getYRot()) * s);
            mc.player.setXRot(mc.player.getXRot() + (pitch - mc.player.getXRot()) * s);
        }
    }
}
