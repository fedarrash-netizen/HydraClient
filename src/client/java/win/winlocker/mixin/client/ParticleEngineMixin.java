package win.winlocker.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(net.minecraft.client.particle.ParticleEngine.class)
public class ParticleEngineMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(Particle particle, CallbackInfo ci) {
        NoRender nr = (NoRender) ModuleManager.getModule(NoRender.class);
        if (nr != null && nr.isEnabled() && NoRender.PARTICLES.get()) {
            ci.cancel();
        }
    }
}
