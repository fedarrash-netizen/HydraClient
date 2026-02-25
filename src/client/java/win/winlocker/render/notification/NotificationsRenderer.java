package win.winlocker.render.notification;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import win.winlocker.utils.render.TextUtils;
import win.winlocker.utils.render.color.ColorUtils;
import win.winlocker.utils.render.DisplayUtils;

import java.util.List;

/**
 * Рендерер уведомлений
 */
public class NotificationsRenderer {
    private static NotificationsRenderer instance;
    private final NotificationManager notificationManager;

    private NotificationsRenderer() {
        this.notificationManager = NotificationManager.getInstance();
    }

    public static NotificationsRenderer getInstance() {
        if (instance == null) {
            instance = new NotificationsRenderer();
        }
        return instance;
    }

    /**
     * Рендер всех уведомлений
     */
    public void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.font == null) {
            return;
        }

        notificationManager.update();

        List<Notification> notifications = notificationManager.getNotifications();
        if (notifications.isEmpty()) {
            return;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        float screenWidth = mc.getWindow().getGuiScaledWidth();
        float startY = 5f;
        float notificationWidth = 250f;
        float padding = 5f;
        float fontSize = 6.5f;

        for (Notification notification : notifications) {
            float height = renderNotification(
                graphics, 
                notification, 
                screenWidth - notificationWidth - 5f, 
                startY, 
                notificationWidth,
                padding,
                fontSize
            );
            startY += height + 5f;
        }

        poseStack.popPose();
    }

    /**
     * Рендер одного уведомления
     */
    private float renderNotification(GuiGraphics graphics, Notification notification, 
                                     float x, float y, float width, float padding, float fontSize) {
        Minecraft mc = Minecraft.getInstance();
        float alpha = notification.getAlpha();

        if (alpha <= 0.01f) {
            return 0;
        }

        // Расчет размеров
        String title = notification.getTitle();
        String message = notification.getMessage();
        String icon = notification.getIcon();

        float titleWidth = TextUtils.width(mc.font, title);
        float messageWidth = TextUtils.width(mc.font, message);
        float iconWidth = TextUtils.width(mc.font, icon);

        float contentWidth = Math.max(titleWidth + iconWidth + padding * 2, messageWidth + padding * 2);
        float notificationHeight = fontSize * 2.5f + padding * 3;

        // Смещение для анимации
        float offsetX = notification.getXOffset();
        float actualX = x + offsetX;

        // Тень (упрощенно)
        int shadowColor = ColorUtils.setAlpha(notification.getColor(), (int) (alpha * 100));
        DisplayUtils.drawRoundedRect(graphics, actualX - 2, y - 2, width + 4, notificationHeight + 4, 8f, shadowColor);

        // Фон
        int bgColor = ColorUtils.setAlpha(ColorUtils.rgba(15, 15, 45, 200), (int) (alpha * 255));
        DisplayUtils.drawRoundedRect(graphics, actualX, y, width, notificationHeight, 4f, bgColor);

        // Цветная полоска слева
        int accentColor = ColorUtils.setAlpha(notification.getColor(), (int) (alpha * 255));
        DisplayUtils.drawRoundedRect(graphics, actualX, y, 3f, notificationHeight, 4f, accentColor);

        // Иконка
        int iconColor = ColorUtils.setAlpha(0xFFFFFFFF, (int) (alpha * 255));
        TextUtils.draw(graphics, mc.font, icon, actualX + padding, y + padding + 1f, iconColor, true);

        // Заголовок
        int titleColor = ColorUtils.setAlpha(0xFFFFFF, (int) (alpha * 255));
        TextUtils.draw(graphics, mc.font, title, actualX + padding * 2 + iconWidth, y + padding, titleColor, true);

        // Сообщение
        int messageColor = ColorUtils.setAlpha(ColorUtils.rgba(210, 210, 210, 255), (int) (alpha * 255));
        TextUtils.draw(graphics, mc.font, message, actualX + padding * 2, y + padding + fontSize + 2f, messageColor, false);

        return notificationHeight;
    }

    /**
     * Получить менеджер уведомлений
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
