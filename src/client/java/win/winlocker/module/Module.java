package win.winlocker.module;

import win.winlocker.DropDown.settings.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final Category category;
    private int key;
    private boolean enabled;
    public boolean wasKeyDown;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public void toggle() {
        this.enabled = !this.enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            toggle();
        }
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public boolean isEnabled() { return enabled; }
    public List<Setting> getSettings() { return settings; }

    protected void addSetting(Setting s) {
        settings.add(s);
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        MISC("Misc");

        private final String name;
        Category(String name) { this.name = name; }
        public String getName() { return name; }
    }
}
