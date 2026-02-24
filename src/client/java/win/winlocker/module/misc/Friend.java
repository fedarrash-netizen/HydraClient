package win.winlocker.module.misc;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.friends.FriendStorage;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class Friend extends Module {
    public final BooleanSetting enabled = new BooleanSetting("Enabled", false);
    public final BooleanSetting protectFriends = new BooleanSetting("Protect Friends", true);
    public final BooleanSetting showFriendNotifications = new BooleanSetting("Show Notifications", true);
    public final BooleanSetting autoAddNearby = new BooleanSetting("Auto Add Nearby", false);
    public final KeyBindSetting addFriendKey = new KeyBindSetting("Add Friend Key", 0);
    public final KeyBindSetting removeFriendKey = new KeyBindSetting("Remove Friend Key", 0);
    
    private static Friend instance;
    private final FriendStorage friendStorage;
    
    public Friend() {
        super("Friend", Category.MISC);
        addSetting(enabled);
        addSetting(protectFriends);
        addSetting(showFriendNotifications);
        addSetting(autoAddNearby);
        addSetting(addFriendKey);
        addSetting(removeFriendKey);
        
        instance = this;
        this.friendStorage = FriendStorage.getInstance();
    }
    
    public static Friend getInstance() {
        return instance;
    }
    
    @Override
    public void onTick() {
        if (!enabled.get()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Автоматическое добавление ближайших игроков
        if (autoAddNearby.get()) {
            handleAutoAddNearby(mc);
        }
    }
    
    private void handleAutoAddNearby(Minecraft mc) {
        for (Player player : mc.level.players()) {
            if (player.equals(mc.player)) continue;
            
            double distance = mc.player.distanceTo(player);
            if (distance <= 10.0 && !friendStorage.isFriend(player)) {
                friendStorage.addFriendFromPlayer(player);
                if (showFriendNotifications.get()) {
                    mc.player.displayClientMessage(
                        Component.literal("§a[Friend] Auto-added " + player.getName().getString() + " as friend"), false);
                }
            }
        }
    }
    
    public boolean isFriend(Player player) {
        return friendStorage.isFriend(player);
    }
    
    public boolean shouldProtectFriends() {
        return enabled.get() && protectFriends.get();
    }
    
    public void addFriend(Player player) {
        if (player != null && !friendStorage.isFriend(player)) {
            friendStorage.addFriendFromPlayer(player);
            if (showFriendNotifications.get()) {
                Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§a[Friend] Added " + player.getName().getString() + " to friends"), false);
            }
        }
    }
    
    public void removeFriend(Player player) {
        if (player != null && friendStorage.isFriend(player)) {
            friendStorage.removeFriendFromPlayer(player);
            if (showFriendNotifications.get()) {
                Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§c[Friend] Removed " + player.getName().getString() + " from friends"), false);
            }
        }
    }
    
    public void toggleFriend(Player player) {
        if (friendStorage.isFriend(player)) {
            removeFriend(player);
        } else {
            addFriend(player);
        }
    }
    
    public FriendStorage getFriendStorage() {
        return friendStorage;
    }
    
    public String getFriendStatus(Player player) {
        if (friendStorage.isFriend(player)) {
            return "§a[Friend]";
        }
        return "";
    }
    
    public int getFriendCount() {
        return friendStorage.getFriendCount();
    }
    
    public void clearFriends() {
        friendStorage.clearFriends();
        if (showFriendNotifications.get()) {
            Minecraft.getInstance().player.displayClientMessage(
                Component.literal("§c[Friend] Cleared all friends"), false);
        }
    }
}
