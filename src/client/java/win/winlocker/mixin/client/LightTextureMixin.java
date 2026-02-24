package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.render.FullBright;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private void onGetBrightness(int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        try {
            FullBright fullBright = FullBright.getInstance();
            if (fullBright != null && fullBright.isEnabled()) {
                // Возвращаем максимальную яркость
                cir.setReturnValue(1.0f);
            }
        } catch (Exception e) {
            // Если ошибка, просто продолжаем без изменений
            System.err.println("[LightTextureMixin] Error in getBrightness: " + e.getMessage());
        }
    }
}
