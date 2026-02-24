package win.winlocker.utils.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DisplayUtils {
    
    public static void drawRoundedRect(GuiGraphics context, float x, float y, float width, float height, float radius, int color) {
        // Простая реализация - рисуем прямоугольник
        // В будущем можно добавить скруглённые углы
        context.fill((int)x, (int)y, (int)(x + width), (int)(y + height), color);
    }
    
    public static void drawRoundedOutline(GuiGraphics context, float x, float y, float width, float height, float radius, float thickness, int color) {
        // Рисуем рамку вокруг прямоугольника
        context.fill((int)x, (int)y, (int)(x + width), (int)(y + thickness), color); // Верх
        context.fill((int)x, (int)(y + height - thickness), (int)(x + width), (int)(y + height), color); // Низ
        context.fill((int)x, (int)y, (int)(x + thickness), (int)(y + height), color); // Лево
        context.fill((int)(x + width - thickness), (int)y, (int)(x + width), (int)(y + height), color); // Право
    }
    
    public static void drawImage(ResourceLocation texture, float x, float y, float width, float height, int color) {
        // Базовая реализация для отрисовки изображения
        // В будущем можно добавить поддержку текстур
    }
    
    public static int rgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
