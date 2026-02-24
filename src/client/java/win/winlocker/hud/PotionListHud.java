package win.winlocker.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import win.winlocker.utils.render.TextUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PotionListHud {
	public void render(GuiGraphics g) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.font == null) {
			return;
		}

		List<MobEffectInstance> effects = new ArrayList<>(mc.player.getActiveEffects());
		if (effects.isEmpty()) {
			return;
		}

		effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration).reversed());

		Font font = mc.font;
		int x = mc.getWindow().getGuiScaledWidth() - 6;
		int y = 6;
		for (MobEffectInstance e : effects) {
			String name = Component.translatable(e.getDescriptionId()).getString();
			int amp = e.getAmplifier();
			if (amp > 0) {
				name = name + " " + (amp + 1);
			}
			String line = name + " " + formatDuration(e.getDuration());
			int w = TextUtils.width(font, line);
			TextUtils.draw(g, font, line, x - w, y, 0xFFFFFFFF, true);
			y += font.lineHeight + 2;
		}
	}

	private static String formatDuration(int ticks) {
		if (ticks < 0) {
			return "";
		}
		int total = ticks / 20;
		int m = total / 60;
		int s = total % 60;
		return String.format("%d:%02d", m, s);
	}
}
