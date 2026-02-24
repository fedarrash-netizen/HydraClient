package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.movement.GuiMove;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        GuiMove mod = (GuiMove) ModuleManager.getModule(GuiMove.class);
        if (mod != null && mod.isEnabled() && Minecraft.getInstance().screen != null) {
            KeyboardInput input = (KeyboardInput) (Object) this;
            KeyboardInputAccessor accessor = (KeyboardInputAccessor) input;
            Minecraft mc = Minecraft.getInstance();
            
            accessor.setLeftImpulse(mc.options.keyLeft.isDown() ? 1.0F : (mc.options.keyRight.isDown() ? -1.0F : 0.0F));
            accessor.setForwardImpulse(mc.options.keyUp.isDown() ? 1.0F : (mc.options.keyDown.isDown() ? -1.0F : 0.0F));
        }
    }
}
