package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

import java.util.List;

public class Targeting extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 2.3.72", List.of("Vanilla", "Grim 2.3.72"));
    private final SliderSetting range = new SliderSetting("Range", 4.0, 1.0, 6.0);
    private int attackDelay = 0;

    public Targeting() {
        super("Targeting", Category.COMBAT);
        addSetting(mode);
        addSetting(range);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

        HitResult hr = mc.hitResult;
        if (hr instanceof EntityHitResult) {
            Entity target = ((EntityHitResult) hr).getEntity();
            if (target instanceof Player && target.isAlive() && mc.player.distanceTo(target) <= range.get()) {
                mc.gameMode.attack(mc.player, target);
                mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                
                if (mode.get().equals("Grim 2.3.72")) {
                    attackDelay = 12 + new java.util.Random().nextInt(5); // Randomized delay for Grim
                } else {
                    attackDelay = 10;
                }
            }
        }
    }
}
