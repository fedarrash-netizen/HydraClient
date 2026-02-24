package win.winlocker.utils.moddetect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModUserManager {
    private static ModUserManager instance;
    private final Map<UUID, Long> modUsers = new HashMap<>();
    private final Map<UUID, String> modUserNames = new HashMap<>();
    private long lastBroadcastTime = 0;
    private static final long BROADCAST_INTERVAL = 5000; // 5 секунд
    private static final long USER_TIMEOUT = 30000; // 30 секунд
    
    public static ModUserManager getInstance() {
        if (instance == null) {
            instance = new ModUserManager();
        }
        return instance;
    }
    
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Отправка broadcast каждые 5 секунд
        if (currentTime - lastBroadcastTime > BROADCAST_INTERVAL) {
            sendModBroadcast();
            lastBroadcastTime = currentTime;
        }
        
        // Очистка старых пользователей
        cleanupOldUsers(currentTime);
    }
    
    private void sendModBroadcast() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // Отправляем специальное сообщение в чат для идентификации
        // Используем невидимые символы или специальный формат
        String broadcastMessage = "§0§k§r§f[WinLocker]§r§0§k";
        mc.player.connection.sendChat(broadcastMessage);
    }
    
    public void onChatMessage(String message, String playerName) {
        // Проверяем, является ли сообщение broadcast от другого пользователя мода
        if (message.contains("§0§k§r§f[WinLocker]§r§0§k")) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            
            // Находим игрока по нику
            for (Player player : mc.level.players()) {
                if (player.getName().getString().equals(playerName) && !player.is(mc.player)) {
                    addModUser(player);
                    break;
                }
            }
        }
    }
    
    public void addModUser(Player player) {
        modUsers.put(player.getUUID(), System.currentTimeMillis());
        modUserNames.put(player.getUUID(), player.getName().getString());
        System.out.println("[ModDetect] Обнаружен пользователь мода: " + player.getName().getString());
    }
    
    public boolean isModUser(Player player) {
        Long lastSeen = modUsers.get(player.getUUID());
        if (lastSeen == null) return false;
        
        // Проверяем, не истекло ли время
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSeen > USER_TIMEOUT) {
            modUsers.remove(player.getUUID());
            modUserNames.remove(player.getUUID());
            return false;
        }
        
        return true;
    }
    
    private void cleanupOldUsers(long currentTime) {
        modUsers.entrySet().removeIf(entry -> currentTime - entry.getValue() > USER_TIMEOUT);
        modUserNames.entrySet().removeIf(entry -> !modUsers.containsKey(entry.getKey()));
    }
    
    public Map<UUID, String> getModUsers() {
        return new HashMap<>(modUserNames);
    }
    
    public int getModUserCount() {
        return modUsers.size();
    }
}
