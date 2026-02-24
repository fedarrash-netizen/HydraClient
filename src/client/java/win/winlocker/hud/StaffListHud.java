package win.winlocker.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.misc.RemoveVisual;

import java.util.ArrayList;
import java.util.List;

public final class StaffListHud {
    private static final String[] STAFF_PREFIXES = {
        "HELPER", "MODERATOR", "MODER", "ST.MODER", "F.MODER", "ST.HELPER",
        "YOUTUBE", "TIKTOK", "D.MODER", "D.ADMIN", "ADMIN", "ST.ADMIN",
        "CURATOR", "GL.ADMIN"
    };

    public void render(GuiGraphics g) {
        RemoveVisual rv = (RemoveVisual) ModuleManager.getModule(RemoveVisual.class);
        if (rv != null && rv.isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        List<String> staff = new ArrayList<>();
        for (Player p : mc.level.players()) {
            String name = p.getName().getString().toUpperCase();
            for (String prefix : STAFF_PREFIXES) {
                if (name.contains(prefix)) {
                    staff.add(p.getName().getString());
                    break;
                }
            }
        }

        if (staff.isEmpty()) return;

        Font font = mc.font;
        int x = 6;
        int y = 100;

        g.drawString(font, "Staff Online:", x, y, 0xFFFF5555, true);
        y += font.lineHeight + 2;

        for (String s : staff) {
            g.drawString(font, "- " + s, x, y, 0xFFFFFFFF, true);
            y += font.lineHeight + 2;
        }
    }
}
