package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.List;

public class NoFall extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Grim 2.3.72", List.of("Vanilla", "Grim 2.3.72", "Packet"));

    public NoFall() {
        super("NoFall", Category.MOVEMENT);
        addSetting(mode);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.fallDistance > 2.5f) {
            if (mode.get().equals("Grim 2.3.72")) {
                // Grim bypass usually involves resetting fall distance or motion
                if (mc.player.fallDistance > 3.0f) {
                    mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, false));
                    mc.player.fallDistance = 0;
                }
            } else if (mode.get().equals("Packet")) {
                mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, false));
            } else if (mode.get().equals("Vanilla")) {
                mc.player.setOnGround(true);
            }
        }
    }
}
