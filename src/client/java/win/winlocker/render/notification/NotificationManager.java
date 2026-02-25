package win.winlocker.render.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Менеджер уведомлений
 */
public class NotificationManager {
    private static NotificationManager instance;
    private final List<Notification> notifications;

    private NotificationManager() {
        this.notifications = new CopyOnWriteArrayList<>();
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Добавить уведомление
     */
    public void addNotification(String title, String message, NotificationType type, long duration) {
        notifications.add(new Notification(title, message, type, duration));
    }

    /**
     * Добавить информационное уведомление
     */
    public void addInfo(String title, String message) {
        addNotification(title, message, NotificationType.INFO, 3000);
    }

    /**
     * Добавить уведомление об успехе
     */
    public void addSuccess(String title, String message) {
        addNotification(title, message, NotificationType.SUCCESS, 2500);
    }

    /**
     * Добавить предупреждение
     */
    public void addWarning(String title, String message) {
        addNotification(title, message, NotificationType.WARNING, 3000);
    }

    /**
     * Добавить уведомление об ошибке
     */
    public void addError(String title, String message) {
        addNotification(title, message, NotificationType.ERROR, 4000);
    }

    /**
     * Добавить уведомление с кастомной длительностью
     */
    public void add(String title, String message, NotificationType type, long duration) {
        addNotification(title, message, type, duration);
    }

    /**
     * Обновить все уведомления
     */
    public void update() {
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.update();
            if (notification.isRemoved()) {
                iterator.remove();
            }
        }
    }

    /**
     * Получить все активные уведомления
     */
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Очистить все уведомления
     */
    public void clear() {
        notifications.clear();
    }

    /**
     * Получить количество активных уведомлений
     */
    public int getCount() {
        return notifications.size();
    }

    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }
}
