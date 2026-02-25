package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.event.EventAttack;
import win.winlocker.event.EventManager;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void onAttack(net.minecraft.world.entity.player.Player player, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            EventAttack event = new EventAttack((LivingEntity) entity);
            EventManager.call(event);
        }
    }
}
