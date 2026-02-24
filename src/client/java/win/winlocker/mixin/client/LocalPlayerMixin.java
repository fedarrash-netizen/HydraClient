package win.winlocker.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.misc.NameProtect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
        String fakeName = NameProtect.getFakeName();
        if (fakeName != null) {
            cir.setReturnValue(Component.literal(fakeName));
        }
    }
}
