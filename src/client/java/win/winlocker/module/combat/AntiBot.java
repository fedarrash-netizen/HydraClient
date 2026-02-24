package win.winlocker.module.combat;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

public class AntiBot extends Module {
    public final BooleanSetting enabled = new BooleanSetting("Enabled", false);
    public final BooleanSetting checkUUID = new BooleanSetting("Check UUID", true);
    public final BooleanSetting checkNames = new BooleanSetting("Check Names", true);
    public final BooleanSetting checkBehavior = new BooleanSetting("Check Behavior", true);
    public final BooleanSetting checkInvalidData = new BooleanSetting("Check Invalid Data", true);
    public final BooleanSetting checkNoMovement = new BooleanSetting("Check No Movement", true);
    public final SliderSetting maxStayTime = new SliderSetting("Max Stay Time", 30.0, 5.0, 120.0);
    public final SliderSetting minMoveDistance = new SliderSetting("Min Move Distance", 0.1, 0.01, 1.0);
    public final BooleanSetting removeBots = new BooleanSetting("Remove Bots", true);
    public final BooleanSetting alertBots = new BooleanSetting("Alert Bots", true);
    
    private static AntiBot instance;
    private final Set<UUID> detectedBots = new HashSet<>();
    private final Set<String> botNames = new HashSet<>(Arrays.asList(
        "NPC", "Bot", "Shop", "Admin", "Moderator", "Helper", "Guard",
        "Villager", "Citizen", "Merchant", "Trader", "Banker",
        "§aNPC", "§6Bot", "§eShop", "§cAdmin", "§bModerator"
    ));
    
    // Типичные UUID ботов от zNPC и других плагинов
    private final Set<String> botUUIDPatterns = new HashSet<>(Arrays.asList(
        "00000000-0000-0000-0000-000000000000", // Пустой UUID
        "0-0-0-0-0", // Нулевой UUID
        "npc-", // Префикс NPC
        "bot-", // Префикс бота
        "fake-" // Префикс фейка
    ));
    
    public AntiBot() {
        super("AntiBot", Category.COMBAT);
        addSetting(enabled);
        addSetting(checkUUID);
        addSetting(checkNames);
        addSetting(checkBehavior);
        addSetting(checkInvalidData);
        addSetting(checkNoMovement);
        addSetting(maxStayTime);
        addSetting(minMoveDistance);
        addSetting(removeBots);
        addSetting(alertBots);
        
        instance = this;
    }
    
    public static AntiBot getInstance() {
        return instance;
    }
    
    public boolean isBot(UUID uuid, String name, boolean hasInvalidData, boolean hasNoMovement, long stayTime) {
        if (!enabled.get()) return false;
        
        // Проверяем, уже ли обнаружен как бот
        if (detectedBots.contains(uuid)) return true;
        
        boolean isBot = false;
        String reason = "";
        
        // Проверка UUID
        if (checkUUID.get() && isBotUUID(uuid)) {
            isBot = true;
            reason = "Invalid UUID";
        }
        
        // Проверка имени
        if (!isBot && checkNames.get() && isBotName(name)) {
            isBot = true;
            reason = "Bot name";
        }
        
        // Проверка невалидных данных
        if (!isBot && checkInvalidData.get() && hasInvalidData) {
            isBot = true;
            reason = "Invalid data";
        }
        
        // Проверка отсутствия движения
        if (!isBot && checkNoMovement.get() && hasNoMovement && stayTime > maxStayTime.get() * 1000) {
            isBot = true;
            reason = "No movement";
        }
        
        // Если обнаружен бот
        if (isBot) {
            detectedBots.add(uuid);
            if (alertBots.get()) {
                sendAlert("Bot detected: " + name + " (" + reason + ")");
            }
            if (removeBots.get()) {
                removeBot(uuid);
            }
        }
        
        return isBot;
    }
    
    private boolean isBotUUID(UUID uuid) {
        String uuidStr = uuid.toString();
        
        // Проверка на пустой UUID
        if (uuidStr.equals("00000000-0000-0000-0000-000000000000")) {
            return true;
        }
        
        // Проверка на паттерны ботов
        for (String pattern : botUUIDPatterns) {
            if (uuidStr.toLowerCase().contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        // Проверка на одинаковые UUID (уникальность)
        if (uuidStr.startsWith("0") || uuidStr.startsWith("1")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isBotName(String name) {
        if (name == null || name.isEmpty()) return true;
        
        String cleanName = name.replaceAll("§[0-9a-fk-or]", "").toLowerCase();
        
        // Проверка на имена ботов
        for (String botName : botNames) {
            if (cleanName.contains(botName.toLowerCase())) {
                return true;
            }
        }
        
        // Проверка на слишком короткие имена
        if (cleanName.length() < 2) {
            return true;
        }
        
        // Проверка на имена только из цифр
        if (cleanName.matches("\\d+")) {
            return true;
        }
        
        // Проверка на повторяющиеся символы
        if (cleanName.matches("(.)\\1+")) {
            return true;
        }
        
        return false;
    }
    
    private void sendAlert(String message) {
        // Отправка алерта в чат
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§c[AntiBot] §f" + message), 
                    false
                );
            }
        } catch (Exception e) {
            System.out.println("[AntiBot] " + message);
        }
    }
    
    private void removeBot(UUID uuid) {
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null && mc.player != null) {
                // Поиск сущности по UUID
                for (net.minecraft.world.entity.Entity entity : mc.level.entitiesForRendering()) {
                    if (entity.getUUID().equals(uuid)) {
                        entity.discard();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Если не удалось удалить, просто логируем
            System.out.println("[AntiBot] Could not remove bot: " + uuid);
        }
    }
    
    public void clearDetectedBots() {
        detectedBots.clear();
    }
    
    public int getBotCount() {
        return detectedBots.size();
    }
    
    public Set<UUID> getDetectedBots() {
        return new HashSet<>(detectedBots);
    }
    
    public boolean isDetectedBot(UUID uuid) {
        return detectedBots.contains(uuid);
    }
    
    // Проверка поведения игрока
    public boolean checkBotBehavior(net.minecraft.world.entity.player.Player player) {
        if (!enabled.get() || !checkBehavior.get()) return false;
        
        // Проверка на отсутствие инвентаря
        if (player.getInventory() == null || player.getInventory().getContainerSize() == 0) {
            return true;
        }
        
        // Проверка на отсутствие здоровья
        if (player.getHealth() <= 0 || player.isDeadOrDying()) {
            return false; // Мертвые игроки - не боты
        }
        
        // Проверка на странные позиции
        if (player.getY() < -100 || player.getY() > 320) {
            return true;
        }
        
        // Проверка на отсутствие нанесенного урона
        if (player.hurtTime == 0 && player.fallDistance == 0) {
            // Это может быть бот, но не точно
        }
        
        return false;
    }
}
