package win.winlocker.utils.antibot;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import win.winlocker.module.combat.AntiBot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiBotUtils {
    private static AntiBotUtils instance;
    private final Map<UUID, Long> playerJoinTime = new HashMap<>();
    private final Map<UUID, net.minecraft.world.phys.Vec3> lastPlayerPosition = new HashMap<>();
    
    public static AntiBotUtils getInstance() {
        if (instance == null) {
            instance = new AntiBotUtils();
        }
        return instance;
    }
    
    public void onPlayerJoin(Player player) {
        UUID uuid = player.getUUID();
        playerJoinTime.put(uuid, System.currentTimeMillis());
        lastPlayerPosition.put(uuid, player.position());
    }
    
    public void onPlayerLeave(Player player) {
        UUID uuid = player.getUUID();
        playerJoinTime.remove(uuid);
        lastPlayerPosition.remove(uuid);
    }
    
    public void updatePlayerPosition(Player player) {
        UUID uuid = player.getUUID();
        lastPlayerPosition.put(uuid, player.position());
    }
    
    public boolean isBot(Player player) {
        AntiBot antiBot = AntiBot.getInstance();
        if (antiBot == null || !antiBot.isEnabled()) {
            return false;
        }
        
        UUID uuid = player.getUUID();
        String name = player.getName().getString();
        
        // Получаем время пребывания
        Long joinTime = playerJoinTime.get(uuid);
        long stayTime = joinTime != null ? System.currentTimeMillis() - joinTime : 0;
        
        // Проверяем движение
        boolean hasNoMovement = checkNoMovement(uuid, player);
        
        // Проверяем невалидные данные
        boolean hasInvalidData = checkInvalidData(player);
        
        // Проверяем поведение
        boolean hasBotBehavior = antiBot.checkBotBehavior(player);
        
        return antiBot.isBot(uuid, name, hasInvalidData, hasNoMovement, stayTime) || hasBotBehavior;
    }
    
    private boolean checkNoMovement(UUID uuid, Player player) {
        net.minecraft.world.phys.Vec3 lastPos = lastPlayerPosition.get(uuid);
        if (lastPos == null) {
            return false;
        }
        
        net.minecraft.world.phys.Vec3 currentPos = player.position();
        double distance = lastPos.distanceToSqr(currentPos);
        
        AntiBot antiBot = AntiBot.getInstance();
        if (antiBot != null) {
            double minDistance = antiBot.minMoveDistance.get();
            return distance < minDistance * minDistance;
        }
        
        return false;
    }
    
    private boolean checkInvalidData(Player player) {
        // Проверка на невалидные данные игрока
        try {
            // Проверка здоровья
            if (player.getHealth() <= 0 || player.getHealth() > player.getMaxHealth()) {
                return true;
            }
            
            // Проверка инвентаря
            if (player.getInventory() == null || player.getInventory().getContainerSize() == 0) {
                return true;
            }
            
            // Проверка позиции
            if (Double.isNaN(player.getX()) || Double.isNaN(player.getY()) || Double.isNaN(player.getZ())) {
                return true;
            }
            
            // Проверка на странные координаты
            if (player.getY() < -100 || player.getY() > 320) {
                return true;
            }
            
            // Проверка на отсутствие имени
            if (player.getName() == null || player.getName().getString().isEmpty()) {
                return true;
            }
            
        } catch (Exception e) {
            return true; // Если произошла ошибка при проверке, считаем ботом
        }
        
        return false;
    }
    
    public void clearData() {
        playerJoinTime.clear();
        lastPlayerPosition.clear();
    }
    
    public int getTrackedPlayerCount() {
        return playerJoinTime.size();
    }
}
