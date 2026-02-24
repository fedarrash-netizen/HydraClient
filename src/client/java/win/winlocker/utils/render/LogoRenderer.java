package win.winlocker.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class LogoRenderer {
    private static LogoRenderer instance;
    
    public static LogoRenderer getInstance() {
        if (instance == null) {
            instance = new LogoRenderer();
        }
        return instance;
    }
    
    private LogoRenderer() {
        // Конструктор
    }
    
    public void renderLogo(GuiGraphics graphics, float x, float y, float size) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            
            // Рисуем простой текстовый логотип "WV"
            String logoText = "§bW§fV";
            
            graphics.drawString(
                mc.font,
                logoText,
                (int) x, (int) y, 0xFFFFFF
            );
            
            // Добавляем декоративную линию под логотипом
            graphics.drawString(
                mc.font,
                "§7§m§r-",
                (int) x, (int) (y + size * 0.8f), 0x55FFFF
            );
            
        } catch (Exception e) {
            System.err.println("[LogoRenderer] Failed to render logo: " + e.getMessage());
        }
    }
}
