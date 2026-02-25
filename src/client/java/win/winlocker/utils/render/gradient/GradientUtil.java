package win.winlocker.utils.render.gradient;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import win.winlocker.utils.render.color.ColorUtils;

/**
 * Утилиты для создания градиентного текста
 */
public class GradientUtil {

    /**
     * Создать градиентный текст (между двумя цветами)
     */
    public static MutableComponent gradient(String text, int startColor, int endColor) {
        if (text == null || text.isEmpty()) {
            return Component.literal("");
        }

        MutableComponent result = Component.literal("");
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            float progress = (float) i / (length - 1);
            int color = ColorUtils.lerpColor(startColor, endColor, progress);
            
            MutableComponent letter = Component.literal(String.valueOf(c));
            letter.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            result.append(letter);
        }

        return result;
    }

    /**
     * Создать градиентный текст (радужный)
     */
    public static MutableComponent rainbow(String text, float offset, float saturation, float brightness) {
        if (text == null || text.isEmpty()) {
            return Component.literal("");
        }

        MutableComponent result = Component.literal("");
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            float progress = (float) i / length;
            int color = ColorUtils.rainbow(offset + progress, saturation, brightness);
            
            MutableComponent letter = Component.literal(String.valueOf(c));
            letter.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            result.append(letter);
        }

        return result;
    }

    /**
     * Создать белый градиентный текст
     */
    public static MutableComponent white(String text) {
        return gradient(text, 0xFFFFFFFF, 0xFFFFFFAA);
    }

    /**
     * Создать золотой градиентный текст
     */
    public static MutableComponent gold(String text) {
        return gradient(text, 0xFFFFD700, 0xFFDAA520);
    }

    /**
     * Создать красный градиентный текст
     */
    public static MutableComponent red(String text) {
        return gradient(text, 0xFFFF6B6B, 0xFFC92A2A);
    }

    /**
     * Создать зеленый градиентный текст
     */
    public static MutableComponent green(String text) {
        return gradient(text, 0x51CF66, 0x2B8A3E);
    }

    /**
     * Создать синий градиентный текст
     */
    public static MutableComponent blue(String text) {
        return gradient(text, 0x74C0FC, 0x1864AB);
    }

    /**
     * Создать фиолетовый градиентный текст
     */
    public static MutableComponent purple(String text) {
        return gradient(text, 0xDA77F2, 0x862E9F);
    }

    /**
     * Создать градиентный текст с кастомными позициями цветов
     */
    public static MutableComponent gradient(String text, int[] colors, float[] positions) {
        if (text == null || text.isEmpty() || colors.length == 0) {
            return Component.literal("");
        }

        MutableComponent result = Component.literal("");
        int length = text.length();

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            float progress = (float) i / (length - 1);
            int color = getColorAtPosition(colors, positions, progress);
            
            MutableComponent letter = Component.literal(String.valueOf(c));
            letter.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            result.append(letter);
        }

        return result;
    }

    private static int getColorAtPosition(int[] colors, float[] positions, float progress) {
        if (progress <= positions[0]) {
            return colors[0];
        }
        if (progress >= positions[positions.length - 1]) {
            return colors[colors.length - 1];
        }

        for (int i = 0; i < positions.length - 1; i++) {
            if (progress >= positions[i] && progress <= positions[i + 1]) {
                float localProgress = (progress - positions[i]) / (positions[i + 1] - positions[i]);
                return ColorUtils.lerpColor(colors[i], colors[i + 1], localProgress);
            }
        }

        return colors[0];
    }

    /**
     * Создать анимированный градиентный текст
     */
    public static MutableComponent animatedGradient(String text, int startColor, int endColor, long speed) {
        float offset = (System.currentTimeMillis() % speed) / (float) speed;
        return gradient(text, startColor, endColor);
    }

    /**
     * Создать градиентный текст с эффектом волны
     */
    public static MutableComponent waveGradient(String text, int startColor, int endColor) {
        if (text == null || text.isEmpty()) {
            return Component.literal("");
        }

        MutableComponent result = Component.literal("");
        long time = System.currentTimeMillis();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float progress = (float) (Math.sin((i + time / 200.0) * 0.5) + 1) / 2;
            int color = ColorUtils.lerpColor(startColor, endColor, progress);
            
            MutableComponent letter = Component.literal(String.valueOf(c));
            letter.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            result.append(letter);
        }

        return result;
    }
}
