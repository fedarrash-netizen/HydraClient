package win.winlocker.utils.render.color;

import java.awt.*;

/**
 * Утилиты для работы с цветами
 */
public class ColorUtils {

    /**
     * Создать цвет из RGBA компонентов
     */
    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Создать цвет из RGB компонентов (alpha = 255)
     */
    public static int rgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Извлечь альфа-компонент
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    /**
     * Извлечь красный компонент
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Извлечь зеленый компонент
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Извлечь синий компонент
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }

    /**
     * Установить альфа-канал цвета
     */
    public static int setAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Установить красный компонент
     */
    public static int setRed(int color, int red) {
        return (color & 0xFF00FFFF) | (red << 16);
    }

    /**
     * Установить зеленый компонент
     */
    public static int setGreen(int color, int green) {
        return (color & 0xFFFF00FF) | (green << 8);
    }

    /**
     * Установить синий компонент
     */
    public static int setBlue(int color, int blue) {
        return (color & 0xFFFFFF00) | blue;
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

        return rgba(r, g, b, a);
    }

    /**
     * Создать радужный цвет
     */
    public static int rainbow(float offset, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % 10000) / 10000.0f + offset;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    /**
     * Создать радужный цвет с кастомной скоростью
     */
    public static int rainbow(float offset, float saturation, float brightness, long speed) {
        float hue = ((System.currentTimeMillis() % speed) / (float) speed) + offset;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    /**
     * Осветлить цвет
     */
    public static int brighter(int color, float factor) {
        int r = Math.min(255, (int) (getRed(color) * (1 + factor)));
        int g = Math.min(255, (int) (getGreen(color) * (1 + factor)));
        int b = Math.min(255, (int) (getBlue(color) * (1 + factor)));
        return rgba(r, g, b, getAlpha(color));
    }

    /**
     * Затемнить цвет
     */
    public static int darker(int color, float factor) {
        int r = Math.max(0, (int) (getRed(color) * (1 - factor)));
        int g = Math.max(0, (int) (getGreen(color) * (1 - factor)));
        int b = Math.max(0, (int) (getBlue(color) * (1 - factor)));
        return rgba(r, g, b, getAlpha(color));
    }

    /**
     * Инвертировать цвет
     */
    public static int invert(int color) {
        return rgba(255 - getRed(color), 255 - getGreen(color), 255 - getBlue(color), getAlpha(color));
    }

    /**
     * Преобразовать Color в int (ARGB)
     */
    public static int toInt(Color color) {
        return rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Преобразовать int (ARGB) в Color
     */
    public static Color toColor(int color) {
        return new Color(color, true);
    }

    /**
     * Получить яркость цвета (0-1)
     */
    public static float getBrightness(int color) {
        return (getRed(color) * 0.299f + getGreen(color) * 0.587f + getBlue(color) * 0.114f) / 255.0f;
    }

    /**
     * Проверить, светлый ли цвет
     */
    public static boolean isLight(int color) {
        return getBrightness(color) > 0.5f;
    }

    /**
     * Смешать два цвета
     */
    public static int blend(int color1, int color2, float ratio) {
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));
        return lerpColor(color1, color2, ratio);
    }

    /**
     * Создать градиент между двумя цветами с прозрачностью
     */
    public static int gradient(int topColor, int bottomColor, float progress) {
        return lerpColor(topColor, bottomColor, progress);
    }

    /**
     * Получить цвет из строки (hex)
     */
    public static int fromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 6) {
            hex = "FF" + hex;
        }
        return (int) Long.parseLong(hex, 16);
    }

    /**
     * Преобразовать цвет в hex строку
     */
    public static String toHex(int color) {
        return String.format("#%08X", color);
    }
}
