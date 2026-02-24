package win.winlocker.mixin.client;

import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.render.CustomFog;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "getFogStart", at = @At("RETURN"), cancellable = true)
    private static void onGetFogStart(CallbackInfoReturnable<Float> cir) {
        CustomFog customFog = CustomFog.getInstance();
        if (customFog != null && customFog.isEnabled()) {
            double power = customFog.getPower();
            if (power > 1.0) {
                float original = cir.getReturnValue();
                cir.setReturnValue((float) (original / power));
            }
        }
    }

    @Inject(method = "getFogEnd", at = @At("RETURN"), cancellable = true)
    private static void onGetFogEnd(CallbackInfoReturnable<Float> cir) {
        CustomFog customFog = CustomFog.getInstance();
        if (customFog != null && customFog.isEnabled()) {
            double power = customFog.getPower();
            if (power > 1.0) {
                float original = cir.getReturnValue();
                cir.setReturnValue((float) (original / power));
            }
        }
    }
}
