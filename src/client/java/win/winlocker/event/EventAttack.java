package win.winlocker.event;

import net.minecraft.world.entity.LivingEntity;

public class EventAttack extends Event {
    private LivingEntity target;

    public EventAttack(LivingEntity target) {
        this.target = target;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }
}
