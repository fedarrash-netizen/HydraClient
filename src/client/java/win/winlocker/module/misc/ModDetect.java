package win.winlocker.module.misc;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.moddetect.ModUserManager;
import win.winlocker.utils.moddetect.ModUserRenderer;

public class ModDetect extends Module {
    public final BooleanSetting enabled = new BooleanSetting("Enabled", true);
    public final BooleanSetting showInWorld = new BooleanSetting("Show In World", true);
    public final BooleanSetting showInPlayerList = new BooleanSetting("Show In Player List", true);
    public final SliderSetting detectionRange = new SliderSetting("Detection Range", 50.0, 10.0, 100.0);
    public final BooleanSetting sendBroadcast = new BooleanSetting("Send Broadcast", true);
    
    private static ModDetect instance;
    private final ModUserManager modUserManager;
    private final ModUserRenderer modUserRenderer;
    
    public ModDetect() {
        super("ModDetect", Category.MISC);
        addSetting(enabled);
        addSetting(showInWorld);
        addSetting(showInPlayerList);
        addSetting(detectionRange);
        addSetting(sendBroadcast);
        
        instance = this;
        this.modUserManager = ModUserManager.getInstance();
        this.modUserRenderer = ModUserRenderer.getInstance();
    }
    
    public static ModDetect getInstance() {
        return instance;
    }
    
    @Override
    public void onTick() {
        if (!enabled.get()) return;
        
        // Обновляем менеджер пользователей
        modUserManager.onTick();
    }
    
    public void onChatMessageReceived(String message, String playerName) {
        if (!enabled.get()) return;
        
        // Передаем сообщение в менеджер для проверки
        modUserManager.onChatMessage(message, playerName);
    }
    
    public boolean shouldShowInWorld() {
        return enabled.get() && showInWorld.get();
    }
    
    public boolean shouldShowInPlayerList() {
        return enabled.get() && showInPlayerList.get();
    }
    
    public boolean shouldSendBroadcast() {
        return enabled.get() && sendBroadcast.get();
    }
    
    public double getDetectionRange() {
        return detectionRange.get();
    }
    
    public ModUserManager getModUserManager() {
        return modUserManager;
    }
    
    public ModUserRenderer getModUserRenderer() {
        return modUserRenderer;
    }
    
    public int getModUserCount() {
        return modUserManager.getModUserCount();
    }
}
