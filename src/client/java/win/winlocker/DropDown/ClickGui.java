package win.winlocker.DropDown;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import win.winlocker.DropDown.settings.*;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.Module;
import win.winlocker.module.misc.ClickGuiSettings;
import net.minecraft.client.gui.components.Button;

import java.util.List;

public class ClickGui extends Screen {
	private final Screen parent;
	private ClickGuiSettings settings;
	private boolean draggingSlider;
	private int contentScroll;
	private KeyBindSetting listeningBind;
	private String searchQuery = "";
	private boolean searchActive = false;

	private Module.Category selectedCategory = Module.Category.RENDER;
	
	// Константы по умолчанию
	private static final int TAB_H = 14;
	private static final int HEADER_H = 22;
	private static final int PAD = 8;

	public ClickGui(Screen parent) {
		super(Component.literal("Puls Visual"));
		this.parent = parent;
		this.settings = (ClickGuiSettings) ModuleManager.getModule(ClickGuiSettings.class);
	}

	public Screen getParent() {
		return parent;
	}

	private Button configButton;

	@Override
	protected void init() {
		this.draggingSlider = false;
		this.contentScroll = 0;
		this.listeningBind = null;
		this.configButton = Button.builder(Component.literal("Config"), button -> {
			Minecraft.getInstance().setScreen(new ConfigGui(this));
		}).bounds(this.width - 80, 10, 80, 20).build();
		this.addWidget(this.configButton);
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);

		int sw = this.width;
		int panelW = settings != null ? settings.getPanelWidth() : 260;
		int panelH = settings != null ? settings.getPanelHeight() : 180;
		int x = (sw - panelW) / 2;
		int y = (this.height - panelH) / 2;

		// Применяем размытие если включено
		boolean blurEnabled = settings != null && settings.isBlurEnabled();
		if (blurEnabled) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(0, 0, 0, 0.5f);
		}

		int bg = 0xC0101010;
		int outline = 0x80101010;
		int header = 0xE01A1A1A;
		int text = 0xFFFFFFFF;

		graphics.fill(x, y, x + panelW, y + panelH, bg);
		graphics.fill(x, y, x + panelW, y + 1, outline);
		graphics.fill(x, y + panelH - 1, x + panelW, y + panelH, outline);
		graphics.fill(x, y, x + 1, y + panelH, outline);
		graphics.fill(x + panelW - 1, y, x + panelW, y + panelH, outline);

		graphics.fill(x, y, x + panelW, y + HEADER_H, header);
		graphics.drawString(this.font, "Puls Visual", x + PAD, y + 7, text, true);

		// Отображение поиска
		boolean searchEnabled = settings != null && settings.isSearchEnabled();
		if (searchActive && searchEnabled) {
			int searchY = y + HEADER_H + 2;
			int searchH = 16;
			graphics.fill(x + PAD, searchY, x + panelW - PAD, searchY + searchH, 0x40222222);
			graphics.fill(x + PAD, searchY, x + panelW - PAD, searchY + 1, 0x60333333);
			graphics.drawString(this.font, "Search: " + searchQuery + (searchActive ? "_" : ""), x + PAD + 4, searchY + 4, 0xFFDDDDDD, false);
			graphics.drawString(this.font, "ESC to close", x + panelW - PAD - this.font.width("ESC to close"), searchY + 4, 0xFF888888, false);
		}

		renderTabs(graphics, x + PAD, y + HEADER_H + (searchActive && searchEnabled ? 20 : 2), mouseX, mouseY);

		int contentX = x + PAD;
		int contentY = y + HEADER_H + TAB_H + (searchActive && searchEnabled ? 28 : 10);
		int contentH = panelH - (HEADER_H + TAB_H + (searchActive && searchEnabled ? 34 : 16));

		graphics.enableScissor(contentX, contentY, contentX + panelW - PAD * 2, contentY + contentH);
		renderTabContent(graphics, contentX, contentY + contentScroll, mouseX, mouseY);
		graphics.disableScissor();
		
		if (blurEnabled) {
			RenderSystem.disableBlend();
		}
	}

	private void renderTabs(GuiGraphics g, int x, int y, int mouseX, int mouseY) {
		int tabX = x;
		for (Module.Category cat : Module.Category.values()) {
			String t = cat.getName();
			int w = this.font.width(t) + 12;
			boolean active = selectedCategory == cat;
			boolean hover = mouseX >= tabX && mouseX <= tabX + w && mouseY >= y && mouseY <= y + TAB_H;
			int bg = active ? 0x602A2A2A : (hover ? 0x40222222 : 0x30181818);
			g.fill(tabX, y, tabX + w, y + TAB_H, bg);
			g.drawString(this.font, t, tabX + 6, y + 3, 0xFFDDDDDD, false);
			tabX += w + 4;
		}
	}

	private void renderTabContent(GuiGraphics g, int x, int y, int mouseX, int mouseY) {
		int cy = y;
		List<Module> modules = ModuleManager.getModulesByCategory(selectedCategory);
		
		// Фильтрация модулей по поисковому запросу
		if (!searchQuery.isEmpty()) {
			modules = modules.stream()
				.filter(m -> m.getName().toLowerCase().contains(searchQuery.toLowerCase()))
				.toList();
		}
		
		if (modules.isEmpty()) {
			g.drawString(this.font, searchQuery.isEmpty() ? "Empty" : "No modules found", x, cy, 0xFF777777, false);
		} else {
			for (Module m : modules) {
				cy = renderModule(g, m, x, cy, mouseX, mouseY);
				cy += 8;
			}
		}
	}

	private int renderModule(GuiGraphics g, Module module, int x, int y, int mouseX, int mouseY) {
		int titleColor = module.isEnabled() ? 0xFF5AA8FF : 0xFFCCCCCC;
		g.drawString(this.font, module.getName(), x, y, titleColor, false);

		int bindW = this.font.width("[" + (module.getKey() == 0 ? "NONE" : GLFW.glfwGetKeyName(module.getKey(), 0)) + "]");
		// Simple bind display next to name if not NONE
		if (module.getKey() != 0) {
			String bName = " [" + getBindName(module.getKey()) + "]";
			g.drawString(this.font, bName, x + this.font.width(module.getName()) + 2, y, 0xFF777777, false);
		}

		int cy = y + 14;
		for (Setting s : module.getSettings()) {
			if (s instanceof BooleanSetting) {
				BooleanSetting bs = (BooleanSetting) s;
				if (bs.isVisible()) {
					renderBooleanSetting(g, bs, x, cy, mouseX, mouseY);
					cy += 14;
				}
			} else if (s instanceof SliderSetting) {
				SliderSetting ss = (SliderSetting) s;
				if (ss.isVisible()) {
					renderSliderSetting(g, ss, x, cy, mouseX, mouseY);
					cy += 18;
				}
			} else if (s instanceof ModeSetting) {
				ModeSetting ms = (ModeSetting) s;
				if (ms.isVisible()) {
					renderModeSetting(g, ms, x, cy, mouseX, mouseY);
					cy += 14;
				}
			} else if (s instanceof KeyBindSetting) {
				KeyBindSetting ks = (KeyBindSetting) s;
				if (ks.isVisible()) {
					renderKeyBindSetting(g, ks, x, cy, mouseX, mouseY, module);
					cy += 14;
				}
			} else if (s instanceof ColorSetting) {
				ColorSetting cs = (ColorSetting) s;
				if (cs.isVisible()) {
					renderColorSetting(g, cs, x, cy, mouseX, mouseY);
					cy += 16;
				}
			}
		}
		return cy;
	}

	private String getBindName(int key) {
		if (key <= 0) return "NONE";
		String n = GLFW.glfwGetKeyName(key, 0);
		if (n != null) return n.toUpperCase();
		return "K" + key;
	}

	private void renderKeyBindSetting(GuiGraphics g, KeyBindSetting s, int x, int y, int mouseX, int mouseY, Module m) {
		int contentW = settings != null ? settings.getContentWidth() : 160;
		int w = contentW;
		int h = 12;
		boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		int bg = hover ? 0x40222222 : 0x30181818;
		g.fill(x, y, x + w, y + h, bg);
		String key = (listeningBind == s) ? "..." : getBindName(m.getKey());
		g.drawString(this.font, "Bind: " + key, x + 4, y + 2, 0xFFDDDDDD, false);
		g.drawString(this.font, "(MMB)", x + w - 4 - this.font.width("(MMB)"), y + 2, 0xFFAAAAAA, false);
	}

	private void renderBooleanSetting(GuiGraphics g, BooleanSetting s, int x, int y, int mouseX, int mouseY) {
		int contentW = settings != null ? settings.getContentWidth() : 160;
		int w = contentW;
		int h = 12;
		boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		int bg = hover ? 0x40222222 : 0x30181818;
		g.fill(x, y, x + w, y + h, bg);
		g.drawString(this.font, s.getName() + ": " + (s.get() ? "ON" : "OFF"), x + 4, y + 2, 0xFFDDDDDD, false);
	}

	private void renderSliderSetting(GuiGraphics g, SliderSetting s, int x, int y, int mouseX, int mouseY) {
		int contentW = settings != null ? settings.getContentWidth() : 160;
		int w = contentW;
		int h = 12;
		int barY = y + 9;
		boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		int bg = hover ? 0x40222222 : 0x30181818;
		g.fill(x, y, x + w, y + h, bg);

		int fillW = (int) Math.round(w * s.getPercent());
		g.fill(x, barY, x + fillW, barY + 2, 0xFF5AA8FF);
		g.fill(x + fillW, barY, x + w, barY + 2, 0xFF1E1E1E);

		g.drawString(this.font, s.getName() + ": " + s.getInt(), x + 4, y + 1, 0xFFDDDDDD, false);
	}

	private void renderModeSetting(GuiGraphics g, ModeSetting s, int x, int y, int mouseX, int mouseY) {
		int contentW = settings != null ? settings.getContentWidth() : 160;
		int w = contentW;
		int h = 12;
		boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		int bg = hover ? 0x40222222 : 0x30181818;
		g.fill(x, y, x + w, y + h, bg);
		g.drawString(this.font, s.getName() + ": " + s.get(), x + 4, y + 2, 0xFFDDDDDD, false);
	}

	private void renderColorSetting(GuiGraphics g, ColorSetting s, int x, int y, int mouseX, int mouseY) {
		int contentW = settings != null ? settings.getContentWidth() : 160;
		int w = contentW;
		int h = 14;
		boolean hover = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		int bg = hover ? 0x40222222 : 0x30181818;
		g.fill(x, y, x + w, y + h, bg);
		
		// Отображение названия
		g.drawString(this.font, s.getName() + ":", x + 4, y + 3, 0xFFDDDDDD, false);
		
		// Цветовой прямоугольник
		int colorBoxSize = 10;
		int colorX = x + w - colorBoxSize - 2;
		int colorY = y + 2;
		g.fill(colorX, colorY, colorX + colorBoxSize, colorY + colorBoxSize, 0xFF000000);
		g.fill(colorX + 1, colorY + 1, colorX + colorBoxSize - 1, colorY + colorBoxSize - 1, s.get());
		
		// HEX значение
		String hex = String.format("#%08X", s.get());
		g.drawString(this.font, hex, x + w - this.font.width(hex) - colorBoxSize - 8, y + 3, 0xFF888888, false);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 || button == 1 || button == 2) {
			int sw = this.width;
			int x = (sw - getPanelWidth()) / 2;
			int y = (this.height - getPanelHeight()) / 2;

			int tabX = x + PAD;
			int tabY = y + HEADER_H + (searchActive ? 20 : 2);
			for (Module.Category cat : Module.Category.values()) {
				String t = cat.getName();
				int w = this.font.width(t) + 12;
				if (hit((int) mouseX, (int) mouseY, tabX, tabY, w, TAB_H)) {
					selectedCategory = cat;
					contentScroll = 0;
					draggingSlider = false;
					return true;
				}
				tabX += w + 4;
			}

			int contentX = x + PAD;
			int contentY = y + HEADER_H + TAB_H + (searchActive ? 28 : 10);
			if (clickSettingsAt(contentX, contentY + contentScroll, (int) mouseX, (int) mouseY, mouseX, button)) {
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			draggingSlider = false;
			activeDraggingSlider = null;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (button == 0 && draggingSlider) {
			int sw = this.width;
			int x = (sw - getPanelWidth()) / 2;
			int contentX = x + PAD;
			SliderSetting active = getActiveDraggingSlider();
			if (active != null) {
				updateSliderFromMouse(active, contentX, getContentWidth(), mouseX);
			}
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	private void updateSliderFromMouse(SliderSetting s, int x, int w, double mouseX) {
		double pct = (mouseX - x) / (double) w;
		s.setPercent(pct);
	}

	private boolean clickSettingsAt(int x, int y, int mx, int my, double mouseXDouble, int button) {
		draggingSlider = false;
		int cy = y;
		List<Module> modules = ModuleManager.getModulesByCategory(selectedCategory);

		// Фильтрация модулей по поисковому запросу
		if (!searchQuery.isEmpty()) {
			modules = modules.stream()
				.filter(m -> m.getName().toLowerCase().contains(searchQuery.toLowerCase()))
				.toList();
		}

		for (Module m : modules) {
			if (hit(mx, my, x, cy, getPanelWidth() - PAD * 2, 12)) {
				if (button == 0) m.toggle();
				return true;
			}
			int nextY = cy + 14;
			for (Setting s : m.getSettings()) {
				if (s instanceof BooleanSetting) {
					BooleanSetting bs = (BooleanSetting) s;
					if (bs.isVisible() && hit(mx, my, x, nextY, getContentWidth(), 12)) {
						bs.toggle();
						return true;
					}
					if (bs.isVisible()) nextY += 14;
				} else if (s instanceof SliderSetting) {
					SliderSetting ss = (SliderSetting) s;
					if (ss.isVisible() && hit(mx, my, x, nextY, getContentWidth(), 12)) {
						draggingSlider = true;
						setActiveDraggingSlider(ss);
						updateSliderFromMouse(ss, x, getContentWidth(), mouseXDouble);
						return true;
					}
					if (ss.isVisible()) nextY += 18;
				} else if (s instanceof ModeSetting) {
					ModeSetting ms = (ModeSetting) s;
					if (ms.isVisible() && hit(mx, my, x, nextY, getContentWidth(), 12)) {
						ms.next();
						return true;
					}
					if (ms.isVisible()) nextY += 14;
				} else if (s instanceof KeyBindSetting) {
					KeyBindSetting ks = (KeyBindSetting) s;
					if (ks.isVisible() && hit(mx, my, x, nextY, getContentWidth(), 12) && button == 2) {
						listeningBind = ks;
						listeningModule = m;
						return true;
					}
					if (ks.isVisible()) nextY += 14;
				} else if (s instanceof ColorSetting) {
					ColorSetting cs = (ColorSetting) s;
					if (cs.isVisible() && hit(mx, my, x, nextY, getContentWidth(), 12) && button == 0) {
						// Открыть ColorPicker при клике
						Minecraft.getInstance().setScreen(new ColorPicker(this, cs));
						return true;
					}
					if (cs.isVisible()) nextY += 16;
				}
			}
			cy = nextY + 8;
		}
		return false;
	}

	private Module listeningModule;

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		int sw = this.width;
		int x = (sw - getPanelWidth()) / 2;
		int y = (this.height - getPanelHeight()) / 2;
		int contentX = x + PAD;
		int contentY = y + HEADER_H + TAB_H + (searchActive ? 28 : 10);
		int contentH = getPanelHeight() - (HEADER_H + TAB_H + (searchActive ? 34 : 16));
		if (!hit((int) mouseX, (int) mouseY, contentX, contentY, getPanelWidth() - PAD * 2, contentH)) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}

		int scrollStep = 12;
		contentScroll += (int) Math.round(verticalAmount * scrollStep);
		clampScroll();
		return true;
	}

	private void clampScroll() {
		int contentBaseY = (this.height - getPanelHeight()) / 2 + HEADER_H + TAB_H + (searchActive ? 28 : 10);
		int viewBottom = (this.height - getPanelHeight()) / 2 + getPanelHeight() - PAD;
		
		int totalH = 0;
		List<Module> modules = ModuleManager.getModulesByCategory(selectedCategory);
		
		// Фильтрация модулей по поисковому запросу
		if (!searchQuery.isEmpty()) {
			modules = modules.stream()
				.filter(m -> m.getName().toLowerCase().contains(searchQuery.toLowerCase()))
				.toList();
		}
		
		for (Module m : modules) {
			totalH += 14; // title
			for (Setting s : m.getSettings()) {
				if (s instanceof BooleanSetting) totalH += 14;
				else if (s instanceof SliderSetting) totalH += 18;
				else if (s instanceof ModeSetting) totalH += 14;
				else if (s instanceof KeyBindSetting) totalH += 14;
			}
			totalH += 8; // gap
		}

		int minScroll = Math.min(0, (viewBottom - contentBaseY) - totalH);
		contentScroll = Math.max(minScroll, Math.min(0, contentScroll));
	}

	private SliderSetting activeDraggingSlider;
	private void setActiveDraggingSlider(SliderSetting s) { this.activeDraggingSlider = s; }
	private SliderSetting getActiveDraggingSlider() { return this.activeDraggingSlider; }
	private static boolean hit(int mx, int my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }

	// Вспомогательные методы для получения размеров
	private int getPanelWidth() {
		return settings != null ? settings.getPanelWidth() : 260;
	}
	
	private int getPanelHeight() {
		return settings != null ? settings.getPanelHeight() : 180;
	}
	
	private int getContentWidth() {
		return settings != null ? settings.getContentWidth() : 160;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Ctrl+F для поиска (только если включен в настройках)
		boolean searchEnabled = settings != null && settings.isSearchEnabled();
		if (keyCode == 70 && (modifiers & 2) != 0 && searchEnabled) { // F key with Ctrl
			searchActive = !searchActive;
			if (!searchActive) {
				searchQuery = "";
			}
			return true;
		}
		
		// ESC для закрытия поиска
		if (searchActive && keyCode == 256) {
			searchActive = false;
			searchQuery = "";
			return true;
		}
		
		// Backspace для удаления символа из поиска
		if (searchActive && searchEnabled && keyCode == 259) {
			if (!searchQuery.isEmpty()) {
				searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
			}
			return true;
		}
		
		// Ввод символов в поиск
		if (searchActive && searchEnabled && keyCode >= 32 && keyCode <= 126) {
			char c = (char) keyCode;
			searchQuery += c;
			return true;
		}
		
		if (listeningBind != null && listeningModule != null) {
			if (keyCode == 256) listeningModule.setKey(0);
			else listeningModule.setKey(keyCode);
			listeningBind = null;
			listeningModule = null;
			return true;
		}
		if (keyCode == 256) {
			Minecraft.getInstance().setScreen(parent);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
