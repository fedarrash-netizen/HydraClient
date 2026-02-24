package win.winlocker.mixin.client;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.accessor.HitColorStateAccessor;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.HitColor;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererHitColorMixin {
    private static HitColor getHitColor() {
        HitColor hitColor = HitColor.getInstance();
        if (hitColor == null) {
            hitColor = (HitColor) ModuleManager.getModule(HitColor.class);
        }
        return hitColor;
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void onExtractRenderState(LivingEntity entity, LivingEntityRenderState renderState, float partialTick, CallbackInfo ci) {
        HitColor hitColor = getHitColor();
        boolean active = hitColor != null && hitColor.shouldTint(entity, renderState.hasRedOverlay);
        ((HitColorStateAccessor) renderState).tloader$setHitColorActive(active);
    }

    @Inject(
            method = "getModelTint(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetModelTint(LivingEntityRenderState renderState, CallbackInfoReturnable<Integer> cir) {
        HitColor hitColor = getHitColor();
        if (hitColor == null || !hitColor.isEnabled()) {
            return;
        }

        if (((HitColorStateAccessor) renderState).tloader$isHitColorActive()) {
            cir.setReturnValue(hitColor.getTintColor());
        }
    }
}
