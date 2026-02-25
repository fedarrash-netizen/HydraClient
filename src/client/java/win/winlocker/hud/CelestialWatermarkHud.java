package win.winlocker.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import win.winlocker.utils.render.DisplayUtils;
import win.winlocker.utils.render.font.Fonts;

/**
 * Портированная под Winlocker версия ватермарки из Hud2 (CELESTIAL).
 * Использует стандартный шрифт Minecraft (ресурсные шрифты из assets/Winlocker
 * будут применяться через json-конфиг шрифтов).
 */
public final class CelestialWatermarkHud {

    private final Minecraft mc = Minecraft.getInstance();

    // Настройки
    private boolean rainbowBackground = true;
    private boolean doubleText = true;
    private float alpha = 1.0f;   // 0..1
    private float speed = 10.0f;  // скорость переливания

    public void setRainbowBackground(boolean rainbowBackground) {
        this.rainbowBackground = rainbowBackground;
    }

    public void setDoubleText(boolean doubleText) {
        this.doubleText = doubleText;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    public void setSpeed(float speed) {
        this.speed = Math.max(1.0f, speed);
    }

    /**
     * Рендер ватермарки в левом верхнем углу.
     */
    public void render(GuiGraphics g) {
        if (mc.player == null || mc.font == null) {
            return;
        }

        float fontSize = 1.0f; // масштаб для Fonts (1.0 = обычный шрифт)
        float posX = 8.0f;
        float posY = 8.0f;

        String name = " CELESTIAL  " +
                "  " + "shelfpaster" +
                "  " + "  " + " UID: 2102" +
                " " + "    " + mc.getFps() + " FPS  ";

        float textWidth = Fonts.getWidth(name, fontSize);
        float width = textWidth + 9.0f;
        float height = 15.0f;

        if (rainbowBackground) {
            drawRainbowClientRectangle(g, posX, posY, width, height);
        } else {
            drawClientRectangle(g, posX, posY, width, height);
        }

        if (doubleText) {
            drawDoubleText(g, name, posX + 3.0f, posY + 3.0f, fontSize);
        } else {
            Fonts.drawText(g, name, posX + 3.0f, posY + 4.0f, 0xFFFFFFFF, fontSize);
        }
    }

    private void drawClientRectangle(GuiGraphics g, float x, float y, float width, float height) {
        int a = (int) (255 * alpha);
        int bg = DisplayUtils.rgba(34, 32, 32, a);

        // Лёгкая "тень"
        DisplayUtils.drawRoundedRect(g, x + 1.0f, y + 1.0f, width, height, 6.0f, DisplayUtils.rgba(0, 0, 0, a / 3));
        // Основной фон
        DisplayUtils.drawRoundedRect(g, x, y, width, height, 6.0f, bg);
    }

    private void drawRainbowClientRectangle(GuiGraphics g, float x, float y, float width, float height) {
        long time = System.currentTimeMillis();
        float timeOffset = (time / (this.speed * 15.0f)) % 360.0f;

        int c1 = hsvToRgb(timeOffset, 0.8f, 1.0f, alpha);
        int c2 = hsvToRgb(timeOffset + 45.0f, 0.8f, 1.0f, alpha);
        int c3 = hsvToRgb(timeOffset + 90.0f, 0.8f, 1.0f, alpha);
        int c4 = hsvToRgb(timeOffset + 135.0f, 0.8f, 1.0f, alpha);

        // Верхняя цветная "рамка"
        float topBarHeight = 3.0f;
        float segmentWidth = (width - 3.1f) / 4.0f;

        DisplayUtils.drawRect(g, x, y, segmentWidth, topBarHeight, c1);
        DisplayUtils.drawRect(g, x + segmentWidth, y, segmentWidth, topBarHeight, c2);
        DisplayUtils.drawRect(g, x + segmentWidth * 2.0f, y, segmentWidth, topBarHeight, c3);
        DisplayUtils.drawRect(g, x + segmentWidth * 3.0f, y, segmentWidth, topBarHeight, c4);

        // Тёмный фон под текст
        int bg = DisplayUtils.rgb(34, 32, 32);
        DisplayUtils.drawRoundedRect(g, x - 0.75f, y + topBarHeight, width - 1.7f, height - topBarHeight + 1.5f, 5.5f, bg);
    }

    private void drawDoubleText(GuiGraphics g, String text, float x, float y, float fontScale) {
        String celestialPart = " CELESTIAL";
        if (!text.startsWith(celestialPart)) {
            Fonts.drawText(g, text, x, y, 0xFFFFFFFF, fontScale);
            return;
        }

        String restPart = text.substring(celestialPart.length());
        float currentX = x;

        long time = System.currentTimeMillis();
        float timeOffset = (time / (this.speed * 15.0f)) % 360.0f;
        int shadowColor = hsvToRgb(timeOffset, 0.8f, 1.0f, 120 / 255.0f);

        // Тень цветом темы
        Fonts.drawText(g, celestialPart, currentX - 1.0f, y + 1.0f, shadowColor, fontScale);
        // Основной белый текст чуть выше и левее
        Fonts.drawText(g, celestialPart, currentX - 2.0f, y, 0xFFFFFFFF, fontScale);

        String arrow = "\u00BB";
        Fonts.drawText(g, arrow, currentX + 41.5f, y, 0xFFC8C8C8, fontScale);
        Fonts.drawText(g, arrow, currentX + 92.5f, y, 0xFFC8C8C8, fontScale);
        Fonts.drawText(g, arrow, currentX + 135.5f, y, 0xFFC8C8C8, fontScale);

        currentX += Fonts.getWidth(celestialPart, fontScale);

        Fonts.drawText(g, restPart, currentX, y, 0xFFFFFFFF, fontScale);
    }

    /**
     * Простой HSV → ARGB (0-360, 0-1, 0-1, alpha 0-1)
     */
    private int hsvToRgb(float h, float s, float v, float alpha01) {
        h = (h % 360.0f + 360.0f) % 360.0f;
        s = Math.max(0.0f, Math.min(1.0f, s));
        v = Math.max(0.0f, Math.min(1.0f, v));

        float c = v * s;
        float x = c * (1.0f - Math.abs((h / 60.0f) % 2.0f - 1.0f));
        float m = v - c;

        float r, g, b;
        if (h < 60.0f)      { r = c; g = x; b = 0f; }
        else if (h < 120.0f){ r = x; g = c; b = 0f; }
        else if (h < 180.0f){ r = 0f; g = c; b = x; }
        else if (h < 240.0f){ r = 0f; g = x; b = c; }
        else if (h < 300.0f){ r = x; g = 0f; b = c; }
        else                { r = c; g = 0f; b = x; }

        int a = (int) (Math.max(0.0f, Math.min(1.0f, alpha01)) * 255.0f);
        int ri = (int) ((r + m) * 255.0f);
        int gi = (int) ((g + m) * 255.0f);
        int bi = (int) ((b + m) * 255.0f);

        return (a << 24) | (ri << 16) | (gi << 8) | bi;
    }
}


