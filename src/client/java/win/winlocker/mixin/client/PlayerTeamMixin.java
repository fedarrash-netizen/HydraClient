package win.winlocker.mixin.client;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.misc.NameProtect;

@Mixin(PlayerTeam.class)
public class PlayerTeamMixin {
    
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Component> cir) {
        String fakeName = NameProtect.getFakeName();
        if (fakeName != null) {
            cir.setReturnValue(Component.literal(fakeName));
        }
    }
}
