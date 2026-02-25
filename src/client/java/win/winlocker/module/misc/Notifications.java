package win.winlocker.module.misc;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.render.notification.NotificationManager;
import win.winlocker.render.notification.NotificationManager.NotificationType;

/**
 * Модуль для уведомлений (Notifications)
 */
public class Notifications extends Module {
    private final BooleanSetting showModules;
    private final BooleanSetting showInfo;
    private final ColorSetting infoColor;
    private final ColorSetting successColor;
    private final ColorSetting warningColor;
    private final ColorSetting errorColor;

    public Notifications() {
        super("Notifications", Category.MISC);

        this.showModules = new BooleanSetting("Show Modules", true);
        this.showInfo = new BooleanSetting("Show Info", true);
        this.infoColor = new ColorSetting("Info Color", 0xFF3498DB);
        this.successColor = new ColorSetting("Success Color", 0xFF2ECC71);
        this.warningColor = new ColorSetting("Warning Color", 0xFFF1C40F);
        this.errorColor = new ColorSetting("Error Color", 0xFFE74C3C);

        addSetting(showModules);
        addSetting(showInfo);
        addSetting(infoColor);
        addSetting(successColor);
        addSetting(warningColor);
        addSetting(errorColor);
    }

    @Override
    public void onEnable() {
        NotificationManager.getInstance().addSuccess("Notifications", "Module enabled!");
    }

    @Override
    public void onDisable() {
        NotificationManager.getInstance().addWarning("Notifications", "Module disabled!");
    }

    public boolean shouldShowModules() {
        return showModules.get();
    }

    public boolean shouldShowInfo() {
        return showInfo.get();
    }

    public int getInfoColor() {
        return infoColor.get();
    }

    public int getSuccessColor() {
        return successColor.get();
    }

    public int getWarningColor() {
        return warningColor.get();
    }

    public int getErrorColor() {
        return errorColor.get();
    }

    /**
     * Показать уведомление о включении модуля
     */
    public void showModuleEnable(String moduleName) {
        if (shouldShowModules()) {
            NotificationManager.getInstance().addSuccess(moduleName, "Enabled");
        }
    }

    /**
     * Показать уведомление о выключении модуля
     */
    public void showModuleDisable(String moduleName) {
        if (shouldShowModules()) {
            NotificationManager.getInstance().addWarning(moduleName, "Disabled");
        }
    }
}
