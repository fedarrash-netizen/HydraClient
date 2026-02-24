package win.winlocker.mixin.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import win.winlocker.accessor.HitColorStateAccessor;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements HitColorStateAccessor {
    @Unique
    private boolean tloader$hitColorActive;

    @Override
    public void tloader$setHitColorActive(boolean active) {
        this.tloader$hitColorActive = active;
    }

    @Override
    public boolean tloader$isHitColorActive() {
        return tloader$hitColorActive;
    }
}
