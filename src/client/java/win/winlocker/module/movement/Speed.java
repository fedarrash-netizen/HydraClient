package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

import java.util.List;

public class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 2.3.72", List.of("Vanilla", "Grim 2.3.72"));
    private final SliderSetting speed = new SliderSetting("Value", 1.2, 1.0, 5.0);

    public Speed() {
        super("Speed", Category.MOVEMENT);
        addSetting(mode);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.onGround()) return;

        if (mc.options.keyUp.isDown()) {
            float yaw = mc.player.getYRot();
            double s = speed.get() * 0.1;
            
            if (mode.get().equals("Grim 2.3.72")) {
                s = Math.min(s, 0.15); // Cap speed for Grim to avoid instant flag
            }

            mc.player.setDeltaMovement(
                -Math.sin(Math.toRadians(yaw)) * s,
                mc.player.getDeltaMovement().y,
                Math.cos(Math.toRadians(yaw)) * s
            );
        }
    }
}
