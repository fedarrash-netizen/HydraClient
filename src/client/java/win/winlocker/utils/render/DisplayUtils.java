package win.winlocker.utils.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class DisplayUtils {

    public static void drawRoundedRect(GuiGraphics context, float x, float y, float width, float height, float radius, int color) {
        context.fill(Math.round(x), Math.round(y), Math.round(x + width), Math.round(y + height), color);
    }

    public static void drawRoundedOutline(GuiGraphics context, float x, float y, float width, float height, float radius, float thickness, int color) {
        int left = Math.round(x);
        int top = Math.round(y);
        int right = Math.round(x + width);
        int bottom = Math.round(y + height);
        int border = Math.max(1, Math.round(thickness));

        context.fill(left, top, right, top + border, color);
        context.fill(left, bottom - border, right, bottom, color);
        context.fill(left, top, left + border, bottom, color);
        context.fill(right - border, top, right, bottom, color);
    }

    public static void drawImage(ResourceLocation texture, float x, float y, float width, float height, int color) {
        // Keep method for compatibility. Image drawing is handled in callers where render type is known.
    }

    public static int drawText(GuiGraphics context, Font font, String text, float x, float y, int color, boolean shadow) {
        return TextUtils.draw(context, font, text, x, y, color, shadow);
    }

    public static int drawCenteredText(GuiGraphics context, Font font, String text, float centerX, float y, int color, boolean shadow) {
        return TextUtils.drawCentered(context, font, text, centerX, y, color, shadow);
    }

    public static int textWidth(Font font, String text) {
        return TextUtils.width(font, text);
    }

    public static int rgb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
