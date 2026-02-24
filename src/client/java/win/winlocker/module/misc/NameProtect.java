package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.StringSetting;
import win.winlocker.module.Module;

import java.util.ArrayList;
import java.util.List;

public class NameProtect extends Module {
    private final StringSetting protectedName = new StringSetting("Protected Name", "");
    private final BooleanSetting exactMatch = new BooleanSetting("Exact Match", false);
    private final BooleanSetting showNotification = new BooleanSetting("Show Notification", true);
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", false);

    private final List<String> detectedPlayers = new ArrayList<>();

    public NameProtect() {
        super("NameProtect", Category.MISC);
        addSetting(protectedName);
        addSetting(exactMatch);
        addSetting(showNotification);
        addSetting(autoDisable);
    }

    @Override
    public void onEnable() {
        detectedPlayers.clear();
    }

    @Override
    public void onDisable() {
        detectedPlayers.clear();
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        String protectName = protectedName.get();
        if (protectName == null || protectName.isEmpty()) return;

        for (Player player : mc.level.players()) {
            if (player.equals(mc.player)) continue;

            String playerName = player.getName().getString();
            boolean isDetected = false;

            if (exactMatch.get()) {
                // Точное совпадение
                if (playerName.equalsIgnoreCase(protectName)) {
                    isDetected = true;
                }
            } else {
                // Частичное совпадение (содержит ник)
                if (playerName.toLowerCase().contains(protectName.toLowerCase())) {
                    isDetected = true;
                }
            }

            if (isDetected && !detectedPlayers.contains(playerName)) {
                detectedPlayers.add(playerName);
                
                if (showNotification.get()) {
                    mc.player.displayClientMessage(
                        Component.literal("§c[NameProtect] Detected: " + playerName), false);
                }

                if (autoDisable.get()) {
                    this.toggle();
                    if (showNotification.get()) {
                        mc.player.displayClientMessage(
                            Component.literal("§c[NameProtect] Module disabled"), false);
                    }
                }
            }
        }
    }

    public boolean isProtectedName(String playerName) {
        String protectName = protectedName.get();
        if (protectName == null || protectName.isEmpty()) return false;

        if (exactMatch.get()) {
            return playerName.equalsIgnoreCase(protectName);
        } else {
            return playerName.toLowerCase().contains(protectName.toLowerCase());
        }
    }

    public List<String> getDetectedPlayers() {
        return new ArrayList<>(detectedPlayers);
    }

    public void clearDetected() {
        detectedPlayers.clear();
    }
}
