package win.winlocker.mixin.client;

import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyboardInput.class)
public interface KeyboardInputAccessor {
    @Invoker("getLeftImpulse")
    float getLeftImpulse();

    @Invoker("setLeftImpulse")
    void setLeftImpulse(float leftImpulse);

    @Invoker("getForwardImpulse")
    float getForwardImpulse();

    @Invoker("setForwardImpulse")
    void setForwardImpulse(float forwardImpulse);
}
