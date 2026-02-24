package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.misc.NameProtect;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin {
    
    @Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        if (!NameProtect.isActive()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        PlayerInfo self = (PlayerInfo) (Object) this;
        if (!mc.player.getUUID().equals(self.getProfile().getId())) {
            return;
        }

        Component original = cir.getReturnValue();
        if (original == null) {
            original = Component.literal(self.getProfile().getName());
        }
        cir.setReturnValue(NameProtect.replace(original));
    }
}
