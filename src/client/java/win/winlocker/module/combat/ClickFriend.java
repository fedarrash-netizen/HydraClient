package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import win.winlocker.DropDown.settings.KeyBindSetting;
import net.minecraft.network.chat.Component;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.friends.FriendStorage;

import java.util.List;

public class ClickFriend extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Click", List.of("Click", "Follow", "Auto"));
    private final KeyBindSetting friendKey = new KeyBindSetting("Friend Key", 0);
    private final SliderSetting range = new SliderSetting("Range", 4.0, 1.0, 8.0);
    private final SliderSetting followSpeed = new SliderSetting("Follow Speed", 1.0, 0.5, 3.0);
    private final SliderSetting clickDelay = new SliderSetting("Click Delay", 0.0, 0.0, 20.0);
    private final BooleanSetting addFriendOnClick = new BooleanSetting("Add Friend On Click", true);
    private final BooleanSetting showFriendStatus = new BooleanSetting("Show Friend Status", true);
    private final BooleanSetting onlyTargetFriends = new BooleanSetting("Only Target Friends", false);
    
    private Player targetFriend = null;
    private boolean isFollowing = false;
    private long lastClickTime = 0;
    private final FriendStorage friendStorage;
    
    public ClickFriend() {
        super("ClickFriend", Category.COMBAT);
        addSetting(mode);
        addSetting(friendKey);
        addSetting(range);
        addSetting(followSpeed);
        addSetting(clickDelay);
        addSetting(addFriendOnClick);
        addSetting(showFriendStatus);
        addSetting(onlyTargetFriends);
        
        this.friendStorage = FriendStorage.getInstance();
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        String currentMode = mode.get();
        
        // Поиск друзей в радиусе
        boolean targetExists = false;
        if (targetFriend != null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity.equals(targetFriend)) {
                    targetExists = true;
                    break;
                }
            }
        }
        
        if (targetFriend == null || !targetExists || mc.player.distanceTo(targetFriend) > range.get()) {
            findNewFriend();
        }
        
        // Обработка режимов
        switch (currentMode) {
            case "Click":
                handleClickMode(mc);
                break;
            case "Follow":
                handleFollowMode(mc);
                break;
            case "Auto":
                handleAutoMode(mc);
                break;
        }
        
        // Сброс цели если она вышла из радиуса
        if (targetFriend != null && mc.player.distanceTo(targetFriend) > range.get()) {
            targetFriend = null;
            isFollowing = false;
        }
        
        // Показываем статус друга
        if (showFriendStatus.get() && targetFriend != null) {
            showFriendStatus(mc);
        }
    }
    
    private void handleClickMode(Minecraft mc) {
        // Проверяем нажатие клавиши друга
        if (friendKey.getKey() > 0 && mc.options.keyAttack.isDown()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime >= (long) (clickDelay.get() * 1000)) {
                if (mc.player.distanceTo(targetFriend) <= range.get()) {
                    // Добавляем в друзья если включено
                    if (addFriendOnClick.get() && !friendStorage.isFriend(targetFriend)) {
                        friendStorage.addFriendFromPlayer(targetFriend);
                        mc.player.displayClientMessage(
                            Component.literal("§a[ClickFriend] Added " + targetFriend.getName().getString() + " to friends"), false);
                    }
                    
                    // Кликаем на друга
                    mc.options.keyAttack.setDown(false);
                    mc.options.keyAttack.setDown(true);
                    mc.options.keyAttack.setDown(false);
                    
                    targetFriend = mc.player;
                    isFollowing = true;
                    
                    mc.player.displayClientMessage(
                        Component.literal("§aNow following " + targetFriend.getName().getString() + " §7[Click to unfollow]"), false);
                    
                    lastClickTime = currentTime;
                }
            }
        }
    }
    
    private void handleFollowMode(Minecraft mc) {
        if (isFollowing && targetFriend != null) {
            // Следуем за другом
            double dx = targetFriend.getX() - mc.player.getX();
            double dz = targetFriend.getZ() - mc.player.getZ();
            double dy = targetFriend.getY() - mc.player.getY();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (distance > 2.0) {
                // Двигаемся к другу
                float speed = (float) followSpeed.get();
                mc.player.setDeltaMovement(dx / distance * speed, dy / distance * speed, dz / distance * speed);
            }
        }
    }
    
    private void handleAutoMode(Minecraft mc) {
        // Автоматически находим ближайшего друга
        findNewFriend();
    }
    
    private void findNewFriend() {
        Minecraft mc = Minecraft.getInstance();
        Player closestFriend = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                double distance = mc.player.distanceTo(player);
                
                // Проверяем что игрок не в режиме спектатора и не это мы сами
                if (!player.isSpectator() && !player.equals(mc.player) && distance < closestDistance) {
                    // Если включено только друзей, проверяем что это друг
                    if (!onlyTargetFriends.get() || friendStorage.isFriend(player)) {
                        closestFriend = player;
                        closestDistance = distance;
                    }
                }
            }
        }
        
        if (closestFriend != null) {
            targetFriend = closestFriend;
            isFollowing = false;
            
            if (mc.player != null) {
                String status = friendStorage.isFriend(closestFriend) ? "§a[Friend] " : "§e[Player] ";
                mc.player.displayClientMessage(
                    Component.literal("§aAuto-targeted " + status + closestFriend.getName().getString()), false);
            }
        }
    }
    
    private void showFriendStatus(Minecraft mc) {
        if (targetFriend != null) {
            String status = friendStorage.isFriend(targetFriend) ? 
                "§a[Friend] " + targetFriend.getName().getString() : 
                "§e[Player] " + targetFriend.getName().getString();
            
            // Можно добавить рендер статуса над головой
        }
    }
    
    public void toggleFriend(Player player) {
        if (friendStorage.isFriend(player)) {
            friendStorage.removeFriendFromPlayer(player);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§c[ClickFriend] Removed " + player.getName().getString() + " from friends"), false);
            }
        } else {
            friendStorage.addFriendFromPlayer(player);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§a[ClickFriend] Added " + player.getName().getString() + " to friends"), false);
            }
        }
    }
    
    public boolean isFriend(Player player) {
        return friendStorage.isFriend(player);
    }
    
    public FriendStorage getFriendStorage() {
        return friendStorage;
    }
    
    public Player getTargetFriend() {
        return targetFriend;
    }
    
    public boolean isFollowing() {
        return isFollowing;
    }
}
