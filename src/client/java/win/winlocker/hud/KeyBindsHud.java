package win.winlocker.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;
import win.winlocker.module.Module;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.ParticlesModule;
import win.winlocker.ClientSettings;
import win.winlocker.utils.render.TextUtils;

import java.util.ArrayList;
import java.util.List;

public final class KeyBindsHud {
	private boolean dragging;
	private int dragOffX;
	private int dragOffY;

	public void render(GuiGraphics g) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.font == null) {
			return;
		}

		List<String> lines = new ArrayList<>();
		for (Module m : ModuleManager.getModules()) {
			if (m.isEnabled()) {
				String keyName = m.getKey() <= 0 ? "" : " [" + getBindName(m.getKey()) + "]";
				lines.add(m.getName() + keyName);
			}
		}

		if (lines.isEmpty()) {
			return;
		}

		Font font = mc.font;
		int x = ClientSettings.keyBindsHudX;
		int y = ClientSettings.keyBindsHudY;
		int w = 0;
		for (String s : lines) {
			w = Math.max(w, TextUtils.width(font, s));
		}
		w += 10;
		int h = 8 + lines.size() * (font.lineHeight + 2);

		boolean inChat = mc.screen instanceof ChatScreen;
		if (inChat) {
			handleDrag(w, h);
		}

		int bg = 0xA0101010;
		int outline = 0x70000000;
		g.fill(x, y, x + w, y + h, bg);
		g.fill(x, y, x + w, y + 1, outline);
		g.fill(x, y + h - 1, x + w, y + h, outline);
		g.fill(x, y, x + 1, y + h, outline);
		g.fill(x + w - 1, y, x + w, y + h, outline);

		TextUtils.draw(g, font, "KeyBinds", x + 5, y + 4, 0xFFFFFFFF, true);
		int ty = y + 4 + font.lineHeight + 2;
		for (String s : lines) {
			TextUtils.draw(g, font, s, x + 5, ty, 0xFFDDDDDD, false);
			ty += font.lineHeight + 2;
		}

		if (inChat) {
			int mx = (int) getMouseXScaled();
			int my = (int) getMouseYScaled();
			boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
			if (hover) {
				TextUtils.draw(g, font, "(drag)", x + w - 5 - TextUtils.width(font, "(drag)"), y + 4, 0xFFAAAAAA, false);
			}
		}
	}

	private String getBindName(int key) {
		if (key <= 0) return "NONE";
		String n = org.lwjgl.glfw.GLFW.glfwGetKeyName(key, 0);
		if (n != null) return n.toUpperCase();
		return "K" + key;
	}

	private void handleDrag(int w, int h) {
		Minecraft mc = Minecraft.getInstance();
		long win = mc.getWindow().getWindow();
		boolean down = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
		int mx = (int) getMouseXScaled();
		int my = (int) getMouseYScaled();

		int x = ClientSettings.keyBindsHudX;
		int y = ClientSettings.keyBindsHudY;

		if (!down) {
			dragging = false;
			return;
		}

		if (!dragging) {
			boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
			if (hover) {
				dragging = true;
				dragOffX = mx - x;
				dragOffY = my - y;
			}
		}

		if (dragging) {
			ClientSettings.keyBindsHudX = mx - dragOffX;
			ClientSettings.keyBindsHudY = my - dragOffY;
		}
	}

	private static double getMouseXScaled() {
		Minecraft mc = Minecraft.getInstance();
		double scale = (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth();
		return mc.mouseHandler.xpos() * scale;
	}

	private static double getMouseYScaled() {
		Minecraft mc = Minecraft.getInstance();
		double scale = (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight();
		return mc.mouseHandler.ypos() * scale;
	}
}
