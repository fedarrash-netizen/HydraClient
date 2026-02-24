package win.winlocker.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Random;

public class AltManagerScreen extends Screen {
	private final Screen parent;
	private EditBox nameBox;
	private int selectedIndex = -1;
	private final Random random = new Random();

	public AltManagerScreen(Screen parent) {
		super(Component.literal("AltManager"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int y = 40;

		this.nameBox = new EditBox(this.font, centerX - 100, y, 200, 20, Component.literal(""));
		this.nameBox.setMaxLength(16);
		this.addRenderableWidget(this.nameBox);
		y += 26;

		this.addRenderableWidget(Button.builder(Component.literal("Add Offline"), b -> {
			String n = nameBox.getValue();
			if (n != null) {
				n = n.trim();
				if (!n.isEmpty()) {
					AltManagerStore.add(new AltAccount(n));
					nameBox.setValue("");
				}
			}
		}).bounds(centerX - 100, y, 98, 20).build());

		this.addRenderableWidget(Button.builder(Component.literal("Add Random"), b -> {
			String n = "Player" + (1000 + random.nextInt(9000));
			AltManagerStore.add(new AltAccount(n));
		}).bounds(centerX + 2, y, 98, 20).build());

		y += 24;
		this.addRenderableWidget(Button.builder(Component.literal("Select"), b -> {
			AltManagerStore.select(selectedIndex);
		}).bounds(centerX - 100, y, 98, 20).build());

		this.addRenderableWidget(Button.builder(Component.literal("Delete"), b -> {
			AltManagerStore.remove(selectedIndex);
			selectedIndex = -1;
		}).bounds(centerX + 2, y, 98, 20).build());

		y += 24;
		this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
			Minecraft.getInstance().setScreen(parent);
		}).bounds(centerX - 100, y, 200, 20).build());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int listX = this.width / 2 - 100;
			int listY = 40 + 26 + 24 + 24 + 8;
			int rowH = 14;
			int mx = (int) mouseX;
			int my = (int) mouseY;
			if (mx >= listX && mx <= listX + 200 && my >= listY && my <= listY + rowH * 8) {
				int idx = (my - listY) / rowH;
				List<AltAccount> accounts = AltManagerStore.getAccounts();
				if (idx >= 0 && idx < accounts.size()) {
					selectedIndex = idx;
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);

		int centerX = this.width / 2;
		graphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFFFF);

		AltAccount selected = AltManagerStore.getSelected();
		String active = selected == null ? "Active: none" : ("Active: " + selected.getName());
		graphics.drawString(this.font, active, centerX - 100, 22, 0xFFCCCCCC, false);

		int listX = centerX - 100;
		int listY = 40 + 26 + 24 + 24 + 8;
		int rowH = 14;
		List<AltAccount> accounts = AltManagerStore.getAccounts();

		graphics.drawString(this.font, "Accounts:", listX, listY - 12, 0xFFCCCCCC, false);
		for (int i = 0; i < Math.min(8, accounts.size()); i++) {
			AltAccount acc = accounts.get(i);
			int y = listY + i * rowH;
			boolean selectedRow = i == selectedIndex;
			int bg = selectedRow ? 0x502A2A2A : 0x30181818;
			graphics.fill(listX, y, listX + 200, y + rowH - 1, bg);
			graphics.drawString(this.font, acc.getName(), listX + 4, y + 2, 0xFFFFFFFF, false);
		}
	}
}
