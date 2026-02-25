package win.winlocker.event;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Событие отрисовки уведомлений
 */
public class EventNotification extends Event {
    private final String title;
    private final String message;
    private final NotificationType type;
    private final long duration;

    public EventNotification(String title, String message, NotificationType type, long duration) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }
}
