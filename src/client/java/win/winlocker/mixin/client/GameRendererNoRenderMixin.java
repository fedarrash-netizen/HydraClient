package win.winlocker.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(GameRenderer.class)
public class GameRendererNoRenderMixin {
    @Shadow
    private ItemStack itemActivationItem;

    private static NoRender getNoRender() {
        NoRender nr = NoRender.getInstance();
        if (nr == null) {
            nr = (NoRender) ModuleManager.getModule(NoRender.class);
        }
        return nr;
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void onBobHurt(PoseStack poseStack, float partialTick, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideHurtCam()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
    private void onRenderItemActivationAnimation(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideTotemAnimation(itemActivationItem)) {
            ci.cancel();
        }
    }
}
