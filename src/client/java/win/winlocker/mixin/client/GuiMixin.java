package win.winlocker.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin {
    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(GuiGraphics guiGraphics, net.minecraft.resources.ResourceLocation resourceLocation, float f, CallbackInfo ci) {
        NoRender nr = (NoRender) ModuleManager.getModule(NoRender.class);
        if (nr != null && nr.isEnabled()) {
            if (NoRender.FIRE.get() && resourceLocation.getPath().contains("fire")) {
                ci.cancel();
            }
            if (NoRender.WATER_LAVA.get() && (resourceLocation.getPath().contains("water") || resourceLocation.getPath().contains("lava"))) {
                ci.cancel();
            }
        }
    }
}
