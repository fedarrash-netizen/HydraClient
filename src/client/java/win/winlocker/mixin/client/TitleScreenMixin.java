package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.alt.AltManagerScreen;

@Mixin(TitleScreen.class)

public abstract class TitleScreenMixin extends Screen {
	protected TitleScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void tloader$addAltManagerButton(CallbackInfo ci) {
		TitleScreen self = (TitleScreen) (Object) this;
		int x = 8;
		int y = self.height - 28;

		this.addRenderableWidget(Button.builder(Component.literal("AltManager"), b -> {
			Minecraft.getInstance().setScreen(new AltManagerScreen(self));
		}).bounds(x, y, 98, 20).build());
	}
}
