package win.winlocker.event;

/**
 * Базовый класс для всех событий
 */
public class Event {
    private boolean cancelled;
    private int priority;

    public Event() {
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
