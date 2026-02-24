package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

import java.util.List;

public class WaterSpeed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 2.3.72", List.of("Vanilla", "Grim 2.3.72"));
    private final SliderSetting speed = new SliderSetting("Value", 1.5, 1.0, 5.0);

    public WaterSpeed() {
        super("WaterSpeed", Category.MOVEMENT);
        addSetting(mode);
        addSetting(speed);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isInWater()) return;

        double s = speed.get() * 0.05;
        mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(
            mc.player.getLookAngle().x * s,
            mc.player.getLookAngle().y * s,
            mc.player.getLookAngle().z * s
        ));
    }
}
