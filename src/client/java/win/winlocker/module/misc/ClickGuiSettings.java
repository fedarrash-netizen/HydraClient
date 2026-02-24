package win.winlocker.module.misc;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

public class ClickGuiSettings extends Module {
    public final SliderSetting guiWidth = new SliderSetting("Width", 260, 100, 500);
    public final SliderSetting guiHeight = new SliderSetting("Height", 180, 100, 400);
    public final SliderSetting contentWidth = new SliderSetting("Content Width", 160, 100, 300);
    public final BooleanSetting blur = new BooleanSetting("Blur", false);
    public final BooleanSetting searchEnabled = new BooleanSetting("Search", true);
    public final ModeSetting guiStyle = new ModeSetting("GUI Style", "CSGUI", 
            java.util.List.of("CSGUI", "DropDown"));
    
    public ClickGuiSettings() {
        super("ClickGuiSettings", Category.MISC);
        addSetting(guiWidth);
        addSetting(guiHeight);
        addSetting(contentWidth);
        addSetting(blur);
        addSetting(searchEnabled);
        addSetting(guiStyle);
    }
    
    public int getPanelWidth() {
        return (int) guiWidth.getInt();
    }
    
    public int getPanelHeight() {
        return (int) guiHeight.getInt();
    }
    
    public int getContentWidth() {
        return (int) contentWidth.getInt();
    }
    
    public boolean isBlurEnabled() {
        return blur.get();
    }
    
    public boolean isSearchEnabled() {
        return searchEnabled.get();
    }
    
    public int getGuiStyle() {
        return guiStyle.get().equals("CSGUI") ? 1 : 2;
    }
}
