package win.winlocker.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {
    private static NoRender getNoRender() {
        NoRender nr = NoRender.getInstance();
        if (nr == null) {
            nr = (NoRender) ModuleManager.getModule(NoRender.class);
        }
        return nr;
    }

    @Inject(method = "renderSkyDisc", at = @At("HEAD"), cancellable = true)
    private void onRenderSkyDisc(float red, float green, float blue, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideCustomSky()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
    private void onRenderSunMoonAndStars(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float partialTick, int moonPhase, float rainLevel, float starBrightness, FogParameters fogParameters, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideCustomSky()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSunriseAndSunset", at = @At("HEAD"), cancellable = true)
    private void onRenderSunriseAndSunset(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, float skyAngle, int rgb, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideCustomSky()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true)
    private void onRenderEndSky(CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideCustomSky()) {
            ci.cancel();
        }
    }
}
