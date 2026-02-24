package win.winlocker.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.render.CustomFog;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
    private static void onSetupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            Vector4f fogColor,
            float renderDistance,
            boolean thickFog,
            float partialTick,
            CallbackInfoReturnable<FogParameters> cir
    ) {
        CustomFog customFog = CustomFog.getInstance();
        if (customFog != null && customFog.isEnabled()) {
            double power = customFog.getPower();
            if (power > 1.0) {
                FogParameters original = cir.getReturnValue();
                if (original != null) {
                    cir.setReturnValue(new FogParameters(
                            (float) (original.start() / power),
                            (float) (original.end() / power),
                            original.shape(),
                            original.red(),
                            original.green(),
                            original.blue(),
                            original.alpha()
                    ));
                }
            }
        }
    }
}
