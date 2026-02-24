package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;

import java.util.HashMap;
import java.util.Map;

public class Finder extends Module {
    
    private final BooleanSetting autoUpdate = new BooleanSetting("Автообновление", true);
    private final SliderSetting updateInterval = new SliderSetting("Интервал обновления (сек)", 5, 1, 30);
    private final BooleanSetting showDistance = new BooleanSetting("Показать дистанцию", true);
    private final BooleanSetting showDimension = new BooleanSetting("Показать измерение", true);
    
    private final Map<String, Vec3> playerPositions = new HashMap<>();
    private long lastUpdateTime = 0;
    private String lastTarget = "";
    
    public Finder() {
        super("Finder", Module.Category.MISC);
        
        addSetting(autoUpdate);
        addSetting(updateInterval);
        addSetting(showDistance);
        addSetting(showDimension);
    }
    
    @Override
    public void onTick() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        // Автообновление позиций
        if (autoUpdate.get()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > updateInterval.get() * 1000) {
                updateAllPlayerPositions();
                lastUpdateTime = currentTime;
            }
        }
    }
    
    @Override
    public void onEnable() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        
        updateAllPlayerPositions();
        lastUpdateTime = System.currentTimeMillis();
    }
    
    @Override
    public void onDisable() {
        playerPositions.clear();
        lastTarget = "";
    }
    
    /**
     * Обрабатывает команду .finder
     */
    public static boolean handleCommand(String message) {
        if (!message.startsWith(".finder")) {
            return false;
        }
        
        Finder finder = getInstance();
        if (finder == null || !finder.isEnabled()) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal("§cFinder не включен!"), false);
            return true;
        }
        
        String[] parts = message.split(" ");
        
        if (parts.length == 1) {
            // .finder - показать всех игроков
            finder.showAllPlayers();
        } else if (parts.length == 2) {
            // .finder <ник> - найти конкретного игрока
            String targetName = parts[1];
            finder.findPlayer(targetName);
        } else {
            // Неверный формат команды
            showUsage();
        }
        
        return true;
    }
    
    private static Finder getInstance() {
        // Получаем экземпляр Finder из ModuleManager
        for (win.winlocker.module.Module module : win.winlocker.module.ModuleManager.getModules()) {
            if (module instanceof Finder) {
                return (Finder) module;
            }
        }
        return null;
    }
    
    private static void showUsage() {
        Minecraft.getInstance().player.displayClientMessage(Component.literal("§6Использование:"), false);
        Minecraft.getInstance().player.displayClientMessage(Component.literal("§7.finder §f- показать всех игроков"), false);
        Minecraft.getInstance().player.displayClientMessage(Component.literal("§7.finder <ник> §f- найти конкретного игрока"), false);
    }
    
    private void showAllPlayers() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        
        player.displayClientMessage(Component.literal("§6=== Игроки онлайн ==="), false);
        
        boolean foundPlayers = false;
        
        for (Player target : level.players()) {
            if (target == player) {
                continue; // Пропускаем себя
            }
            
            String targetName = target.getName().getString();
            Vec3 targetPos = target.position();
            
            // Сохраняем позицию
            playerPositions.put(targetName, targetPos);
            
            // Показываем информацию об игроке
            String info = formatPlayerInfo(targetName, targetPos, player.position());
            player.displayClientMessage(Component.literal(info), false);
            
            foundPlayers = true;
        }
        
        if (!foundPlayers) {
            player.displayClientMessage(Component.literal("§7Другие игроки не найдены"), false);
        }
    }
    
    private void findPlayer(String targetName) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        
        // Ищем игрока по имени
        Player target = null;
        for (Player p : level.players()) {
            if (p.getName().getString().equalsIgnoreCase(targetName)) {
                target = p;
                break;
            }
        }
        
        if (target == null) {
            player.displayClientMessage(Component.literal("§cИгрок '" + targetName + "' не найден"), false);
            return;
        }
        
        if (target == player) {
            player.displayClientMessage(Component.literal("§7Вы не можете найти сами себя"), false);
            return;
        }
        
        // Получаем позицию цели
        Vec3 targetPos = target.position();
        Vec3 playerPos = player.position();
        
        // Сохраняем позицию
        playerPositions.put(targetName, targetPos);
        lastTarget = targetName;
        
        // Показываем детальную информацию
        player.displayClientMessage(Component.literal("§6=== Найден игрок: " + targetName + " ==="), false);
        player.displayClientMessage(Component.literal(formatPlayerInfo(targetName, targetPos, playerPos)), false);
        
        // Дополнительная информация
        double distance = player.distanceTo(target);
        player.displayClientMessage(Component.literal("§7Дистанция: §f" + String.format("%.1f", distance) + " блоков"), false);
        
        // Направление
        String direction = getDirection(playerPos, targetPos);
        player.displayClientMessage(Component.literal("§7Направление: §f" + direction), false);
        
        // Высота
        player.displayClientMessage(Component.literal("§7Высота: §f" + String.format("%.1f", targetPos.y) + " блоков"), false);
        
        // Проверка на одну высоту
        boolean sameHeight = Math.abs(targetPos.y - playerPos.y) < 2;
        if (sameHeight) {
            player.displayClientMessage(Component.literal("§aИгрок на вашей высоте!"), false);
        }
        
        // Проверка на близость
        if (distance < 10) {
            player.displayClientMessage(Component.literal("§aИгрок очень близко!"), false);
        } else if (distance < 50) {
            player.displayClientMessage(Component.literal("§eИгрок в радиусе 50 блоков"), false);
        } else {
            player.displayClientMessage(Component.literal("§cИгрок далеко (>50 блоков)"), false);
        }
    }
    
    private String formatPlayerInfo(String playerName, Vec3 targetPos, Vec3 playerPos) {
        StringBuilder info = new StringBuilder();
        
        // Имя игрока
        info.append("§a").append(playerName).append("§7: ");
        
        // Координаты
        info.append("§fX:").append(String.format("%.1f", targetPos.x))
            .append(" Y:").append(String.format("%.1f", targetPos.y))
            .append(" Z:").append(String.format("%.1f", targetPos.z));
        
        // Дистанция
        if (showDistance.get()) {
            double distance = playerPos.distanceTo(targetPos);
            info.append(" §7(§f").append(String.format("%.1f", distance)).append("м§7)");
        }
        
        // Измерение
        if (showDimension.get() && Minecraft.getInstance().level != null) {
            String dimension = getDimensionName(Minecraft.getInstance().level);
            info.append(" §7[").append(dimension).append("]");
        }
        
        return info.toString();
    }
    
    private String getDirection(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        
        double angle = Math.toDegrees(Math.atan2(dz, dx));
        angle = (angle + 360) % 360;
        
        if (angle >= 337.5 || angle < 22.5) {
            return "Восток (E)";
        } else if (angle >= 22.5 && angle < 67.5) {
            return "Северо-Восток (NE)";
        } else if (angle >= 67.5 && angle < 112.5) {
            return "Север (N)";
        } else if (angle >= 112.5 && angle < 157.5) {
            return "Северо-Запад (NW)";
        } else if (angle >= 157.5 && angle < 202.5) {
            return "Запад (W)";
        } else if (angle >= 202.5 && angle < 247.5) {
            return "Юго-Запад (SW)";
        } else if (angle >= 247.5 && angle < 292.5) {
            return "Юг (S)";
        } else {
            return "Юго-Восток (SE)";
        }
    }
    
    private String getDimensionName(Level level) {
        if (level.dimension().location().getPath().contains("nether")) {
            return "Nether";
        } else if (level.dimension().location().getPath().contains("end")) {
            return "End";
        } else {
            return "Overworld";
        }
    }
    
    private void updateAllPlayerPositions() {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        
        Level level = Minecraft.getInstance().level;
        
        for (Player target : level.players()) {
            if (target != Minecraft.getInstance().player) {
                String targetName = target.getName().getString();
                playerPositions.put(targetName, target.position());
            }
        }
    }
    
    /**
     * Получает последнюю найденную позицию игрока
     */
    public Vec3 getPlayerPosition(String playerName) {
        return playerPositions.get(playerName);
    }
    
    /**
     * Получает всех игроков с их позициями
     */
    public Map<String, Vec3> getAllPlayerPositions() {
        return new HashMap<>(playerPositions);
    }
    
    /**
     * Получает последнюю цель поиска
     */
    public String getLastTarget() {
        return lastTarget;
    }
    
    public String getDisplayInfo() {
        if (!lastTarget.isEmpty()) {
            return "Цель: " + lastTarget;
        }
        return playerPositions.size() + " игроков";
    }
}
