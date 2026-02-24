package win.winlocker.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import win.winlocker.utils.render.DisplayUtils;
import win.winlocker.utils.render.TextUtils;

public final class TargetHud {
	public void render(GuiGraphics g) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.font == null) {
			return;
		}

		HitResult hr = mc.hitResult;
		if (!(hr instanceof EntityHitResult)) {
			return;
		}

		Entity target = ((EntityHitResult) hr).getEntity();
		if (!(target instanceof Player)) {
			return;
		}

		Player p = (Player) target;
		Font font = mc.font;
		String name = p.getName().getString();
		float hp = p.getHealth() + p.getAbsorptionAmount();
		String hpText = "HP / " + String.format("%.1f", hp);
		
		// Определяем привилегию (можно настроить под свой сервер)
		String privilege = getPrivilege(name);

		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();

		// Размеры HUD
		int width = 140;
		int height = 40;
		int x = sw / 2 - width / 2;
		int y = sh - 80;
		
		// Скругленный фон как на фото
		int bgColor = DisplayUtils.rgb(24, 24, 27); // Темный фон
		int borderColor = DisplayUtils.rgb(72, 52, 212); // Фиолетовая рамка
		
		DisplayUtils.drawRoundedRect(g, x, y, width, height, 8, bgColor);
		DisplayUtils.drawRoundedOutline(g, x, y, width, height, 8, 1, borderColor);

		// Отрисовка головы игрока
		ResourceLocation skin;
		if (p instanceof AbstractClientPlayer) {
			skin = ((AbstractClientPlayer) p).getSkin().texture();
		} else {
			skin = ResourceLocation.withDefaultNamespace("textures/entity/steve.png");
		}
		
		RenderSystem.enableBlend();
		// Голова игрока
		int headSize = 24;
		int headX = x + 8;
		int headY = y + height / 2 - headSize / 2;
		
		// Рисуем голову с масштабированием
		g.pose().pushPose();
		g.pose().scale(headSize / 8.0f, headSize / 8.0f, 1.0f);
		g.blit(RenderType::guiTextured, skin, 
			(int)(headX * 8.0f / headSize), 
			(int)(headY * 8.0f / headSize), 
			8.0f, 8.0f, 8, 8, 64, 64);
		g.blit(RenderType::guiTextured, skin, 
			(int)(headX * 8.0f / headSize), 
			(int)(headY * 8.0f / headSize), 
			40.0f, 8.0f, 8, 8, 64, 64);
		g.pose().popPose();

		// Текст - имя и привилегия
		int textX = headX + headSize + 8;
		int nameY = y + 10;
		
		// Имя игрока (белый)
		TextUtils.draw(g, font, name, textX, nameY, 0xFFFFFFFF, true);
		
		// Привилегия (цветная)
		int privilegeColor = getPrivilegeColor(privilege);
		TextUtils.draw(g, font, privilege, textX + TextUtils.width(font, name) + 5, nameY, privilegeColor, true);

		// HP текст
		int hpY = nameY + 12;
		TextUtils.draw(g, font, hpText, textX, hpY, 0xFFAAAAAA, false);

		// Полоска здоровья
		int barWidth = width - 60;
		int barHeight = 4;
		int barX = textX;
		int barY = hpY + 12;
		
		// Фон полоски
		DisplayUtils.drawRoundedRect(g, barX, barY, barWidth, barHeight, 2, DisplayUtils.rgb(60, 60, 60));
		
		// Заполненная часть полоски (фиолетовая как на фото)
		float healthPercent = Math.min(hp / 20.0f, 1.0f); // Макс HP 20
		int fillWidth = (int)(barWidth * healthPercent);
		if (fillWidth > 0) {
			DisplayUtils.drawRoundedRect(g, barX, barY, fillWidth, barHeight, 2, DisplayUtils.rgb(147, 51, 234));
		}

		// Броня над HUD (если есть)
		renderArmor(g, p, x, y - 15);
	}
	
	private void renderArmor(GuiGraphics g, Player player, int x, int y) {
		int armorX = x + 50;
		for (ItemStack armor : player.getArmorSlots()) {
			if (!armor.isEmpty()) {
				g.renderItem(armor, armorX, y);
				armorX += 18;
			}
		}
	}
	
	private String getPrivilege(String playerName) {
		// Здесь можно добавить логику определения привилегии
		// Пока заглушка - можно настроить под свой сервер
		if (playerName.toLowerCase().contains("admin") || playerName.toLowerCase().contains("элита")) {
			return "Элита";
		} else if (playerName.toLowerCase().contains("baron") || playerName.toLowerCase().contains("барон")) {
			return "Барон";
		} else if (playerName.toLowerCase().contains("vip")) {
			return "VIP";
		} else {
			return "Игрок";
		}
	}
	
	private int getPrivilegeColor(String privilege) {
		switch (privilege) {
			case "Элита": return DisplayUtils.rgb(147, 51, 234); // Фиолетовый
			case "Барон": return DisplayUtils.rgb(234, 179, 8); // Золотой
			case "VIP": return DisplayUtils.rgb(34, 197, 94); // Зеленый
			default: return DisplayUtils.rgb(156, 163, 175); // Серый
		}
	}
}
