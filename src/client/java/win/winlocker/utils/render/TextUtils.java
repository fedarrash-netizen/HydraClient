package win.winlocker.utils.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для работы с текстом
 */
public final class TextUtils {
    private TextUtils() {
    }

    // ========== Basic Drawing ==========

    public static int draw(GuiGraphics graphics, Font font, String text, float x, float y, int color) {
        return draw(graphics, font, text, x, y, color, false);
    }

    public static int draw(GuiGraphics graphics, Font font, String text, float x, float y, int color, boolean shadow) {
        if (graphics == null || font == null || text == null || text.isEmpty()) {
            return 0;
        }
        return graphics.drawString(font, text, Math.round(x), Math.round(y), color, shadow);
    }

    public static int drawCentered(GuiGraphics graphics, Font font, String text, float centerX, float y, int color) {
        return drawCentered(graphics, font, text, centerX, y, color, false);
    }

    public static int drawCentered(GuiGraphics graphics, Font font, String text, float centerX, float y, int color, boolean shadow) {
        int width = width(font, text);
        return draw(graphics, font, text, centerX - (width / 2.0f), y, color, shadow);
    }

    public static int drawRight(GuiGraphics graphics, Font font, String text, float rightX, float y, int color, boolean shadow) {
        int width = width(font, text);
        return draw(graphics, font, text, rightX - width, y, color, shadow);
    }

    // ========== Metrics ==========

    public static int width(Font font, String text) {
        if (font == null || text == null || text.isEmpty()) {
            return 0;
        }
        return font.width(text);
    }

    public static int height(Font font) {
        if (font == null) {
            return 0;
        }
        return font.lineHeight;
    }

    public static int lineHeight(Font font) {
        if (font == null) {
            return 0;
        }
        return font.lineHeight;
    }

    // ========== Text Wrapping & Truncation ==========

    public static String ellipsize(Font font, String text, int maxWidth) {
        if (font == null || text == null) {
            return "";
        }
        if (maxWidth <= 0 || width(font, text) <= maxWidth) {
            return text;
        }

        final String suffix = "...";
        int suffixWidth = width(font, suffix);
        if (suffixWidth >= maxWidth) {
            return suffix;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char next = text.charAt(i);
            String candidate = builder.toString() + next;
            if (width(font, candidate) + suffixWidth > maxWidth) {
                break;
            }
            builder.append(next);
        }
        return builder + suffix;
    }

    /**
     * Разбить текст на строки по максимальной ширине
     */
    public static List<String> wrap(Font font, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (font == null || text == null || text.isEmpty()) {
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int testWidth = width(font, testLine);

            if (testWidth <= maxWidth) {
                currentLine.append(currentLine.isEmpty() ? "" : " ").append(word);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    /**
     * Получить общую высоту многострочного текста
     */
    public static int wrappedHeight(Font font, List<String> lines) {
        if (font == null || lines == null || lines.isEmpty()) {
            return 0;
        }
        return lines.size() * font.lineHeight;
    }

    /**
     * Получить максимальную ширину среди строк
     */
    public static int wrappedWidth(Font font, List<String> lines) {
        if (font == null || lines == null || lines.isEmpty()) {
            return 0;
        }
        int maxWidth = 0;
        for (String line : lines) {
            int w = width(font, line);
            if (w > maxWidth) {
                maxWidth = w;
            }
        }
        return maxWidth;
    }

    // ========== Color Utilities ==========

    /**
     * Создать цвет из RGB компонентов
     */
    public static int rgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Создать цвет из RGBA компонентов
     */
    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Извлечь красный компонент из цвета
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Извлечь зеленый компонент из цвета
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Извлечь синий компонент из цвета
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }

    /**
     * Извлечь альфа-компонент из цвета
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    /**
     * Установить альфа-компонент цвета
     */
    public static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Интерполировать между двумя цветами
     */
    public static int lerpColor(int color1, int color2, float progress) {
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        int a1 = getAlpha(color1);
        int r1 = getRed(color1);
        int g1 = getGreen(color1);
        int b1 = getBlue(color1);

        int a2 = getAlpha(color2);
        int r2 = getRed(color2);
        int g2 = getGreen(color2);
        int b2 = getBlue(color2);

        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Создать градиентный цвет с анимацией (rainbow эффект)
     */
    public static int rainbowColor(float offset, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % 10000) / 10000.0f + offset;
        return java.awt.Color.HSBtoRGB(hue, saturation, brightness);
    }

    // ========== Fade Animation ==========

    /**
     * Вычислить цвет с учетом fade-in анимации
     */
    public static int fadeColor(int targetColor, long startTime, long duration) {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsed / duration);

        int alpha = (int) (progress * 255);
        int targetAlpha = getAlpha(targetColor);
        int actualAlpha = (int) (alpha * (targetAlpha / 255.0f));

        return withAlpha(targetColor, actualAlpha);
    }

    /**
     * Вычислить прозрачность для fade-in/out анимации
     */
    public static float fadeAlpha(long startTime, long duration, boolean fadeIn) {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsed / duration);

        if (fadeIn) {
            return progress;
        }
        return 1.0f - progress;
    }

    // ========== Scrolling Text ==========

    /**
     * Позиция для скроллящегося текста
     */
    public static float scrollTextOffset(String text, Font font, int maxWidth, long time, int speed) {
        int textWidth = width(font, text);
        if (textWidth <= maxWidth) {
            return 0;
        }

        int scrollDistance = textWidth - maxWidth;
        long cycleTime = (scrollDistance * 2L * speed);
        long position = time % cycleTime;

        if (position < scrollDistance * speed) {
            return -(float) position / speed;
        } else if (position < scrollDistance * speed + 500) {
            return -scrollDistance;
        } else if (position < 2L * scrollDistance * speed + 500) {
            return -(scrollDistance - (float) (position - scrollDistance * speed - 500) / speed);
        } else {
            return 0;
        }
    }
}
