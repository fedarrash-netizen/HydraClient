package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.StringSetting;
import win.winlocker.module.Module;

public class NameProtect extends Module {
    public static final StringSetting fakeName = new StringSetting("Fake Name", "");
    public static final BooleanSetting enabled = new BooleanSetting("Enabled", false);
    
    private static NameProtect instance;

    public NameProtect() {
        super("NameProtect", Category.MISC);
        addSetting(enabled);
        addSetting(fakeName);
        instance = this;
    }
    
    public static NameProtect getInstance() {
        return instance;
    }

    @Override
    public void onTick() {
        if (!enabled.get()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        String fakeNameValue = fakeName.get();
        if (fakeNameValue == null || fakeNameValue.isEmpty()) {
            enabled.set(false);
        }
    }
    
    public static String getFakeName() {
        if (instance == null || !enabled.get()) return null;
        String name = fakeName.get();
        return (name != null && !name.isEmpty()) ? name : null;
    }
    
    public static boolean isActive() {
        return instance != null && enabled.get() && !fakeName.isEmpty();
    }
}
