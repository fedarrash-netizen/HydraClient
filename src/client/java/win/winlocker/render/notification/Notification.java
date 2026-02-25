package win.winlocker.render.notification;

import win.winlocker.utils.render.color.ColorUtils;
import win.winlocker.utils.render.Animation;
import win.winlocker.utils.render.AnimationUtils;
import win.winlocker.render.notification.NotificationManager.NotificationType;

/**
 * Класс уведомления
 */
public class Notification {
    private final String title;
    private final String message;
    private final NotificationType type;
    private final long duration;
    private final long createTime;
    private final Animation fadeAnimation;
    private final Animation scaleAnimation;
    private float xOffset;
    private boolean removed;

    public Notification(String title, String message, NotificationType type, long duration) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.duration = duration;
        this.createTime = System.currentTimeMillis();
        this.fadeAnimation = new Animation(0f, 300, AnimationUtils.EASE_OUT_QUAD);
        this.scaleAnimation = new Animation(0f, 200, AnimationUtils.EASE_OUT_BACK);
        this.xOffset = 300f;
        this.removed = false;

        // Запуск анимации появления
        this.fadeAnimation.setTarget(1f, 300, AnimationUtils.EASE_OUT_QUAD);
        this.scaleAnimation.setTarget(1f, 200, AnimationUtils.EASE_OUT_BACK);
        this.xOffset = 0f;
    }

    /**
     * Обновить уведомление
     */
    public void update() {
        fadeAnimation.update();
        scaleAnimation.update();

        long elapsed = System.currentTimeMillis() - createTime;
        if (elapsed >= duration) {
            // Начало анимации исчезновения
            if (fadeAnimation.getTarget() > 0) {
                fadeAnimation.setTarget(0f, 200, AnimationUtils.EASE_IN_QUAD);
            }
        }

        // Удаление после завершения анимации
        if (elapsed >= duration + 200 && fadeAnimation.getValue() <= 0.01f) {
            removed = true;
        }
    }

    /**
     * Проверить, можно ли удалить уведомление
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * Получить прозрачность
     */
    public float getAlpha() {
        return fadeAnimation.getValue();
    }

    /**
     * Получить масштаб
     */
    public float getScale() {
        return scaleAnimation.getValue();
    }

    /**
     * Получить смещение по X
     */
    public float getXOffset() {
        return xOffset;
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

    public long getCreateTime() {
        return createTime;
    }

    /**
     * Получить цвет уведомления
     */
    public int getColor() {
        switch (type) {
            case SUCCESS:
                return ColorUtils.rgba(46, 204, 113, 255);
            case WARNING:
                return ColorUtils.rgba(241, 196, 15, 255);
            case ERROR:
                return ColorUtils.rgba(231, 76, 60, 255);
            default:
                return ColorUtils.rgba(52, 152, 219, 255);
        }
    }

    /**
     * Получить иконку уведомления
     */
    public String getIcon() {
        switch (type) {
            case SUCCESS:
                return "✓";
            case WARNING:
                return "⚠";
            case ERROR:
                return "✕";
            default:
                return "ℹ";
        }
    }
}
