package win.winlocker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.glfw.GLFW;
import win.winlocker.DropDown.ClickGui;
import win.winlocker.config.ConfigManager;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.misc.RemoveVisual;
import win.winlocker.module.Module;
import win.winlocker.hud.KeyBindsHud;
import win.winlocker.hud.PotionListHud;
import win.winlocker.hud.TargetHud;
import win.winlocker.hud.StaffListHud;
import win.winlocker.render.ItemRadius;
import win.winlocker.module.render.BlockOverlay;
import win.winlocker.module.misc.ModDetect;
import win.winlocker.utils.moddetect.ModUserRenderer;
import win.winlocker.module.render.ChinaHat;
import win.winlocker.render.ChinaHatRenderer;
import win.winlocker.module.combat.AntiBot;
import win.winlocker.utils.antibot.AntiBotUtils;
import win.winlocker.module.render.FullBright;
import win.winlocker.utils.math.MathUtil;
import win.winlocker.utils.render.DisplayUtils;
import win.winlocker.utils.render.LogoRenderer;
import win.winlocker.utils.render.TextUtils;

import java.time.format.DateTimeFormatter;

public class TLoaderClient implements ClientModInitializer {
	private static KeyMapping CLICKGUI_KEY;
	private static boolean clickGuiKeyWasDown = false;
	private static final StarParticlesOverlay STAR_PARTICLES_OVERLAY = new StarParticlesOverlay();
	private static final KeyBindsHud KEY_BINDS_HUD = new KeyBindsHud();
	private static final PotionListHud POTION_LIST_HUD = new PotionListHud();
	private static final TargetHud TARGET_HUD = new TargetHud();
	private static final StaffListHud STAFF_LIST_HUD = new StaffListHud();
	private static final ItemRadius ITEM_RADIUS = new ItemRadius();

	private static boolean particlesKeyWasDown;
	private static boolean targetingKeyWasDown;
	private static boolean botAimKeyWasDown;

	@Override
	public void onInitializeClient() {
		ModuleManager.init();
		ConfigManager.loadConfig(); // Загружаем конфиг при старте
		
		HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
			renderClientHud(guiGraphics);
			renderOverlays(guiGraphics);
		});

		WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
                    PoseStack poseStack = context.matrixStack();
                    MultiBufferSource bufferSource = context.consumers();
                    float tickDelta = 0.0f; // Временно

                    ITEM_RADIUS.onRender3D(poseStack, bufferSource, tickDelta);

                    // Рендер BlockOverlay
                    BlockOverlay blockOverlay = (BlockOverlay) ModuleManager.getModule(BlockOverlay.class);
                    if (blockOverlay != null && blockOverlay.isEnabled()) {
                        blockOverlay.renderWorld(poseStack, bufferSource);
                    }

                    // Рендер пользователей мода
                    ModDetect modDetect = (ModDetect) ModuleManager.getModule(ModDetect.class);
                    if (modDetect != null && modDetect.shouldShowInWorld()) {
                        ModUserRenderer renderer = modDetect.getModUserRenderer();
                        if (renderer != null) {
                            // Создаем GuiGraphics из контекста
                            GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(),
                                    Minecraft.getInstance().renderBuffers().bufferSource());
                            renderer.renderModUserTags(guiGraphics, tickDelta);
                        }
                    }

                    // Рендер китайских шляп
                    ChinaHatRenderer hatRenderer = ChinaHatRenderer.getInstance();
                    if (hatRenderer != null) {
                        hatRenderer.renderChinaHats(poseStack, bufferSource, tickDelta);
                    }
		});

		CLICKGUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.tloader.clickgui",
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				"category.tloader"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean down = CLICKGUI_KEY.isDown();
			if (down && !clickGuiKeyWasDown) {
				client.setScreen(new ClickGui(null));
			}
			clickGuiKeyWasDown = down;
			
			// Обновление AntiBot
			AntiBotUtils antiBotUtils = AntiBotUtils.getInstance();
			if (antiBotUtils != null && client.player != null && client.level != null) {
				// Обновляем позиции всех игроков
				for (net.minecraft.world.entity.player.Player player : client.level.players()) {
					antiBotUtils.updatePlayerPosition(player);
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			handleModuleKeybinds();
			
			ModuleManager.getModules().stream()
				.filter(Module::isEnabled)
				.forEach(Module::onTick);
		});

		// Сохраняем конфиг при выключении
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ConfigManager.needsSave()) {
				ConfigManager.saveConfig();
			}
		});
	}

	private static void handleModuleKeybinds() {
		Minecraft client = Minecraft.getInstance();
		if (client.screen == null) {
			for (Module m : ModuleManager.modules) {
				if (m.getKey() != 0) {
					boolean down = GLFW.glfwGetKey(client.getWindow().getWindow(), m.getKey()) == GLFW.GLFW_PRESS;
					if (down && !m.wasKeyDown) {
						m.toggle();
					}
					m.wasKeyDown = down;
				}
			}
		}
	}

	private static final DateTimeFormatter HUD_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");

	private static void renderClientHud(GuiGraphics context) {
		RemoveVisual rv = (RemoveVisual) ModuleManager.getModule(RemoveVisual.class);
		if (rv != null && rv.isEnabled()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.font == null) {
			return;
		}

		// Новый стильный WaterMark с правильным позиционированием
		float x = 15f;
		float y = 15f;
		float height = 30f; // Увеличим высоту для нормального размещения
		float radius = 4f;
		float logoSize = 23f;

		float paddingLeft = 5f;
		float logoPadding = 2f;
		float paddingRight = 4f;
		float statsPadding = 8f; // Увеличим отступ

		String title = "Pulse Visuals";
		String subtitle = "PulseVisuals.pro";

		// Уменьшим размеры текста
		float titleSize = 7f;
		float subSize = 5f;
		float statsSize = 5f;

		float titleWidth = TextUtils.width(mc.font, title);
		float subWidth = TextUtils.width(mc.font, subtitle);
		float textWidth = Math.max(titleWidth, subWidth);

		int fps = mc.getFps();
		int ping = MathUtil.calculatePing();

		String pingText = ping + " ms";
		String fpsText = fps + " fps";

		float pingTextWidth = TextUtils.width(mc.font, pingText);
		float fpsTextWidth = TextUtils.width(mc.font, fpsText);
		float statsTextWidth = Math.max(pingTextWidth, fpsTextWidth);

		float width = logoSize + logoPadding + paddingLeft + textWidth + statsPadding + statsTextWidth + paddingRight;

		// Цвета для пастеров
		int bgColor = DisplayUtils.rgb(36, 36, 36);
		int textColor = DisplayUtils.rgb(255, 255, 255);

		int pingColor = ping < 80 ? DisplayUtils.rgb(0, 255, 120) :
				ping < 150 ? DisplayUtils.rgb(255, 200, 0) :
						DisplayUtils.rgb(255, 80, 80);

		int fpsColor = DisplayUtils.rgb(80, 160, 255);

		DisplayUtils.drawRoundedRect(context, x, y, width, height, radius, bgColor);

		// Рендерим логотип в начале WaterMark
		LogoRenderer logoRenderer = LogoRenderer.getInstance();
		if (logoRenderer != null) {
			logoRenderer.renderLogo(context, x + logoPadding, y + (height - logoSize) / 2f, logoSize);
		}

		float textX = x + logoSize + logoPadding + paddingLeft;
		float titleY = y + 6f; // Фиксированная позиция для заголовка
		float subY = y + 16f; // Фиксированная позиция для подзаголовка

		// Рендерим основной текст
		TextUtils.draw(context, mc.font, title, textX, titleY, textColor, true);
		TextUtils.draw(context, mc.font, subtitle, textX, subY, textColor, true);

		// Позиционируем статистику справа без наложения
		float statsRightX = x + width - paddingRight - statsTextWidth;
		
		float pingY = titleY;
		TextUtils.draw(context, mc.font, pingText, statsRightX, pingY, pingColor, true);

		float fpsY = subY;
		TextUtils.draw(context, mc.font, fpsText, statsRightX, fpsY, fpsColor, true);
	}

	private static void renderOverlays(GuiGraphics context) {
		RemoveVisual rv = (RemoveVisual) ModuleManager.getModule(RemoveVisual.class);
		if (rv != null && rv.isEnabled()) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return;
		}
		boolean allowHud = client.screen == null || client.screen instanceof ChatScreen;
		if (!allowHud) {
			return;
		}

		if (client.screen == null) {
			STAR_PARTICLES_OVERLAY.render(context);
			POTION_LIST_HUD.render(context);
			TARGET_HUD.render(context);
			STAFF_LIST_HUD.render(context);
		}
		KEY_BINDS_HUD.render(context);
	}

	private static void toggleClickGui() {
		RemoveVisual rv = (RemoveVisual) ModuleManager.getModule(RemoveVisual.class);
		if (rv != null && rv.isEnabled()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Screen current = mc.screen;
		if (current instanceof ClickGui) {
			mc.setScreen(((ClickGui) current).getParent());
			return;
		}
		mc.setScreen(new ClickGui(current));
	}
}
