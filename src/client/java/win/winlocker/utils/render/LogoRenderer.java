package win.winlocker.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class LogoRenderer {
    private static LogoRenderer instance;

    public static LogoRenderer getInstance() {
        if (instance == null) {
            instance = new LogoRenderer();
        }
        return instance;
    }

    private LogoRenderer() {
    }

    public void renderLogo(GuiGraphics graphics, float x, float y, float size) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.font == null) {
            return;
        }

        int baseX = Math.round(x);
        int baseY = Math.round(y + Math.max(0.0f, (size - mc.font.lineHeight) / 2.0f));

        String left = "W";
        String right = "V";
        int leftWidth = TextUtils.width(mc.font, left);

        TextUtils.draw(graphics, mc.font, left, baseX, baseY, 0xFF55FFFF, true);
        TextUtils.draw(graphics, mc.font, right, baseX + leftWidth - 1, baseY, 0xFFFFFFFF, true);

        int underlineY = baseY + mc.font.lineHeight;
        TextUtils.draw(graphics, mc.font, "_", baseX + 1, underlineY - 2, 0xAA55FFFF, false);
    }
}
