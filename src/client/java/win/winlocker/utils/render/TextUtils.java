package win.winlocker.utils.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class TextUtils {
    private TextUtils() {
    }

    public static int draw(GuiGraphics graphics, Font font, String text, float x, float y, int color) {
        return draw(graphics, font, text, x, y, color, false);
    }

    public static int draw(GuiGraphics graphics, Font font, String text, float x, float y, int color, boolean shadow) {
        if (graphics == null || font == null || text == null || text.isEmpty()) {
            return 0;
        }
        return graphics.drawString(font, text, Math.round(x), Math.round(y), color, shadow);
    }

    public static int drawCentered(GuiGraphics graphics, Font font, String text, float centerX, float y, int color, boolean shadow) {
        int width = width(font, text);
        return draw(graphics, font, text, centerX - (width / 2.0f), y, color, shadow);
    }

    public static int width(Font font, String text) {
        if (font == null || text == null || text.isEmpty()) {
            return 0;
        }
        return font.width(text);
    }

    public static int lineHeight(Font font) {
        if (font == null) {
            return 0;
        }
        return font.lineHeight;
    }

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
}
