package win.winlocker.mixin.client;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.render.NoRender;

@Mixin(CloudRenderer.class)
public class CloudRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderClouds(int color, CloudStatus cloudStatus, float cloudHeight, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Vec3 cameraPos, float ticks, CallbackInfo ci) {
        NoRender nr = NoRender.getInstance();
        if (nr == null) {
            nr = (NoRender) ModuleManager.getModule(NoRender.class);
        }
        if (nr != null && nr.shouldHideClouds()) {
            ci.cancel();
        }
    }
}
