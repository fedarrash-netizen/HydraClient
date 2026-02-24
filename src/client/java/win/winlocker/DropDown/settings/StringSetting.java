package win.winlocker.DropDown.settings;

public class StringSetting extends Setting {
    private String value;

    public StringSetting(String name, String value) {
        super(name);
        this.value = value;
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = value;
    }
    
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
