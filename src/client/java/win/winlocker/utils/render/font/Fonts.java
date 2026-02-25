package win.winlocker.utils.render.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Утилиты для работы со шрифтами
 */
public class Fonts {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Получить стандартный шрифт Minecraft
     */
    public static Font getFont() {
        return mc.font;
    }

    /**
     * Нарисовать текст
     */
    public static int drawText(GuiGraphics graphics, String text, float x, float y, int color, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        int result = graphics.drawString(font, text, (int) (x / scale), (int) (y / scale), color, true);

        poseStack.popPose();
        return result;
    }

    /**
     * Нарисовать текст по центру
     */
    public static int drawCenteredText(GuiGraphics graphics, String text, float centerX, float y, int color, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        int width = (int) (font.width(text) * scale);
        int result = graphics.drawString(font, text, (int) ((centerX - width / 2.0f) / scale), (int) (y / scale), color, true);

        poseStack.popPose();
        return result;
    }

    /**
     * Нарисовать текст по центру с Component
     */
    public static int drawCenteredText(GuiGraphics graphics, Component text, float centerX, float y, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        int width = font.width(text);
        int result = graphics.drawString(font, text, (int) ((centerX - width / 2.0f) / scale), (int) (y / scale), 0xFFFFFF, true);

        poseStack.popPose();
        return result;
    }

    /**
     * Нарисовать текст справа
     */
    public static int drawRightText(GuiGraphics graphics, String text, float rightX, float y, int color, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0f);

        int width = (int) (font.width(text) * scale);
        int result = graphics.drawString(font, text, (int) ((rightX - width) / scale), (int) (y / scale), color, true);

        poseStack.popPose();
        return result;
    }

    /**
     * Получить ширину текста
     */
    public static float getWidth(String text, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }
        return font.width(text) * scale;
    }

    /**
     * Получить высоту текста
     */
    public static float getHeight(float scale) {
        Font font = getFont();
        if (font == null) {
            return 0;
        }
        return font.lineHeight * scale;
    }

    /**
     * Получить ширину текста Component
     */
    public static float getWidth(Component text) {
        Font font = getFont();
        if (font == null || text == null) {
            return 0;
        }
        return font.width(text);
    }

    /**
     * Обрезать текст с многоточием
     */
    public static String trim(String text, float maxWidth, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return "";
        }

        if (getWidth(text, scale) <= maxWidth) {
            return text;
        }

        String suffix = "...";
        while (getWidth(text + suffix, scale) > maxWidth && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }

        return text + suffix;
    }

    /**
     * Разбить текст на строки
     */
    public static String[] wrap(String text, float maxWidth, float scale) {
        Font font = getFont();
        if (font == null || text == null) {
            return new String[0];
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (getWidth(testLine, scale) <= maxWidth) {
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

        return lines.toArray(new String[0]);
    }
}
