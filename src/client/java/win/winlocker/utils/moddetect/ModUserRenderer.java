package win.winlocker.utils.moddetect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import win.winlocker.utils.moddetect.ModUserManager;

public class ModUserRenderer {
    private static ModUserRenderer instance;
    private final ModUserManager modUserManager;
    
    public static ModUserRenderer getInstance() {
        if (instance == null) {
            instance = new ModUserRenderer();
        }
        return instance;
    }
    
    public ModUserRenderer() {
        this.modUserManager = ModUserManager.getInstance();
    }
    
    public void renderModUserTags(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        Font font = mc.font;
        
        // Проходим по всем игрокам в мире
        for (Player player : mc.level.players()) {
            if (player == mc.player) continue; // Пропускаем себя
            
            // Проверяем, является ли игрок пользователем мода
            if (modUserManager.isModUser(player)) {
                renderModUserTag(graphics, player, font);
            }
        }
    }
    
    private void renderModUserTag(GuiGraphics graphics, Player player, Font font) {
        // Получаем позицию игрока
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        double distance = Math.sqrt(Math.pow(x - Minecraft.getInstance().player.getX(), 2) + 
                                   Math.pow(y - Minecraft.getInstance().player.getY(), 2) + 
                                   Math.pow(z - Minecraft.getInstance().player.getZ(), 2));
        
        // Не рисуем, если игрок слишком далеко
        if (distance > 50) return;
        
        // Получаем позицию для рендера (над головой игрока)
        double tagY = player.getEyeHeight() + 0.5;
        
        // Преобразуем мировые координаты в экранные
        if (isPlayerVisible(player, tagY)) {
            // Рисуем синий значок "P"
            String tag = "§bP";
            float tagWidth = font.width(tag);
            float tagX = (float) (player.getX() - tagWidth / 2);
            float tagY_screen = (float) (player.getY() + tagY);
            
            // Рендерим текст
            graphics.drawString(font, tag, (int)tagX, (int)tagY_screen, 0x55FFFF); // Синий цвет
        }
    }
    
    private boolean isPlayerVisible(Player player, double yOffset) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        
        // Простая проверка на видимость (можно улучшить)
        double dx = player.getX() - mc.player.getX();
        double dy = (player.getY() + yOffset) - (mc.player.getY() + mc.player.getEyeHeight());
        double dz = player.getZ() - mc.player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        return distance < 50; // Видимость в пределах 50 блоков
    }
    
    public void renderPlayerListTag(GuiGraphics graphics, Player player, int x, int y) {
        if (modUserManager.isModUser(player)) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            
            // Рисуем синий "P" рядом с ником в списке игроков
            String tag = "§bP";
            graphics.drawString(font, tag, x - 15, y, 0x55FFFF);
        }
    }
}
