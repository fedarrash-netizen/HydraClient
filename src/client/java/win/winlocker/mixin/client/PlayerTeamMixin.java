package win.winlocker.mixin.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.misc.NameProtect;

@Mixin(net.minecraft.world.scores.PlayerTeam.class)
public class PlayerTeamMixin {

    @Inject(method = "formatNameForTeam", at = @At("HEAD"), cancellable = true)
    private static void onFormatNameForTeam(Team team, Component name, CallbackInfoReturnable<MutableComponent> cir) {
        if (!NameProtect.isActive() || name == null) {
            return;
        }

        String replaced = NameProtect.replace(name.getString());
        if (replaced != null && !replaced.equals(name.getString())) {
            cir.setReturnValue(Component.literal(replaced));
        }
    }
}
