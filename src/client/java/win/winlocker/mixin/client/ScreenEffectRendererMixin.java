package win.winlocker.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    private static NoRender getNoRender() {
        NoRender nr = NoRender.getInstance();
        if (nr == null) {
            nr = (NoRender) ModuleManager.getModule(NoRender.class);
        }
        return nr;
    }

    @Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
    private static void onRenderWater(Minecraft minecraft, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideWaterLavaOverlay()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void onRenderFire(PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr != null && nr.shouldHideFireOverlay()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderTex", at = @At("HEAD"), cancellable = true)
    private static void onRenderTex(TextureAtlasSprite textureAtlasSprite, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        NoRender nr = getNoRender();
        if (nr == null || textureAtlasSprite == null || textureAtlasSprite.contents() == null) {
            return;
        }

        ResourceLocation textureId = textureAtlasSprite.contents().name();
        if (nr.shouldHideBlockOverlay(textureId)) {
            ci.cancel();
        }
    }
}
