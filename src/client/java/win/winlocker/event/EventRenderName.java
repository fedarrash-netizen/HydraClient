package win.winlocker.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Событие отрисовки имени сущности (для NameTags)
 */
public class EventRenderName extends Event {
    private final Entity entity;
    private final double x;
    private final double y;
    private final double z;
    private boolean cancelled;

    public EventRenderName(Entity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.cancelled = false;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public LivingEntity getLivingEntity() {
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }
}
