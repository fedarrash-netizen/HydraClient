package win.winlocker.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для продвинутого рендеринга текста с поддержкой стилей
 */
public class Text {
    private final String content;
    private int color;
    private boolean shadow;
    private float scale;
    private TextStyle style;

    public Text(String content) {
        this.content = content;
        this.color = 0xFFFFFFFF;
        this.shadow = false;
        this.scale = 1.0f;
        this.style = TextStyle.NORMAL;
    }

    public Text(String content, int color) {
        this.content = content;
        this.color = color;
        this.shadow = false;
        this.scale = 1.0f;
        this.style = TextStyle.NORMAL;
    }

    public Text(String content, int color, boolean shadow) {
        this.content = content;
        this.color = color;
        this.shadow = shadow;
        this.scale = 1.0f;
        this.style = TextStyle.NORMAL;
    }

    /**
     * Нарисовать текст в указанных координатах
     */
    public void draw(GuiGraphics graphics, float x, float y) {
        if (content == null || content.isEmpty()) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        if (font == null) {
            return;
        }

        float scaledX = x;
        float scaledY = y;

        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            scaledX = x / scale;
            scaledY = y / scale;
        }

        graphics.drawString(font, content, Math.round(scaledX), Math.round(scaledY), color, shadow);

        if (scale != 1.0f) {
            graphics.pose().popPose();
        }
    }

    /**
     * Нарисовать текст по центру
     */
    public void drawCentered(GuiGraphics graphics, float centerX, float y) {
        Font font = Minecraft.getInstance().font;
        if (font == null) {
            return;
        }

        float scaledCenterX = centerX;
        float scaledY = y;

        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            scaledCenterX = centerX / scale;
            scaledY = y / scale;
        }

        int width = getWidth();
        graphics.drawString(font, content, Math.round(scaledCenterX - width / 2.0f), Math.round(scaledY), color, shadow);

        if (scale != 1.0f) {
            graphics.pose().popPose();
        }
    }

    /**
     * Нарисовать текст с выравниванием вправо
     */
    public void drawRightAligned(GuiGraphics graphics, float rightX, float y) {
        Font font = Minecraft.getInstance().font;
        if (font == null) {
            return;
        }

        float scaledRightX = rightX;
        float scaledY = y;

        if (scale != 1.0f) {
            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1.0f);
            scaledRightX = rightX / scale;
            scaledY = y / scale;
        }

        int width = getWidth();
        graphics.drawString(font, content, Math.round(scaledRightX - width), Math.round(scaledY), color, shadow);

        if (scale != 1.0f) {
            graphics.pose().popPose();
        }
    }

    /**
     * Получить ширину текста
     */
    public int getWidth() {
        Font font = Minecraft.getInstance().font;
        if (font == null || content == null) {
            return 0;
        }
        return (int) (font.width(content) * scale);
    }

    /**
     * Получить высоту текста
     */
    public int getHeight() {
        Font font = Minecraft.getInstance().font;
        if (font == null) {
            return 0;
        }
        return (int) (font.lineHeight * scale);
    }

    /**
     * Получить содержимое текста
     */
    public String getContent() {
        return content;
    }

    /**
     * Установить цвет текста
     */
    public Text withColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * Установить тень для текста
     */
    public Text withShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * Установить масштаб текста
     */
    public Text withScale(float scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Установить стиль текста
     */
    public Text withStyle(TextStyle style) {
        this.style = style;
        return this;
    }

    /**
     * Получить текущий цвет
     */
    public int getColor() {
        return color;
    }

    /**
     * Проверить, включена ли тень
     */
    public boolean hasShadow() {
        return shadow;
    }

    /**
     * Получить текущий масштаб
     */
    public float getScale() {
        return scale;
    }

    /**
     * Получить текущий стиль
     */
    public TextStyle getStyle() {
        return style;
    }

    @Override
    public String toString() {
        return content;
    }

    /**
     * Стили текста
     */
    public enum TextStyle {
        NORMAL,
        BOLD,
        ITALIC,
        UNDERLINE,
        STRIKETHROUGH
    }

    /**
     * Построитель текста для удобного создания
     */
    public static class Builder {
        private StringBuilder content;
        private int color;
        private boolean shadow;
        private float scale;

        public Builder() {
            this.content = new StringBuilder();
            this.color = 0xFFFFFFFF;
            this.shadow = false;
            this.scale = 1.0f;
        }

        public Builder append(String text) {
            content.append(text);
            return this;
        }

        public Builder append(Component component) {
            content.append(component.getString());
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Text build() {
            Text text = new Text(content.toString(), color, shadow);
            text.withScale(scale);
            return text;
        }
    }

    /**
     * Разбить текст на строки по максимальной ширине
     */
    public static List<String> wrapText(String text, Font font, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int testWidth = font.width(testLine);

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
     * Обрезать текст с добавлением многоточия
     */
    public static String truncate(String text, Font font, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        if (font.width(text) <= maxWidth) {
            return text;
        }

        String suffix = "...";
        int suffixWidth = font.width(suffix);

        if (suffixWidth >= maxWidth) {
            return suffix;
        }

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            String test = result.toString() + c;
            if (font.width(test) + suffixWidth > maxWidth) {
                break;
            }
            result.append(c);
        }

        return result + suffix;
    }

    /**
     * Создать градиентный цвет для текста (возвращает средний цвет)
     */
    public static int gradientColor(int color1, int color2, float progress) {
        float r1 = (color1 >> 16) & 0xFF;
        float g1 = (color1 >> 8) & 0xFF;
        float b1 = color1 & 0xFF;

        float r2 = (color2 >> 16) & 0xFF;
        float g2 = (color2 >> 8) & 0xFF;
        float b2 = color2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
