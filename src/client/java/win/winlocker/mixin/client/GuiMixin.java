package win.winlocker.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin {
    private static NoRender getNoRender() {
        NoRender nr = NoRender.getInstance();
        if (nr == null) {
            nr = (NoRender) ModuleManager.getModule(NoRender.class);
        }
        return nr;
    }

    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(GuiGraphics guiGraphics, net.minecraft.resources.ResourceLocation resourceLocation, float f, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldCancelOverlay(resourceLocation)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void onRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideStatusEffects()) {
            ci.cancel();
        }
    }
}
