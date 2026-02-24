package win.winlocker.DropDown.settings;

public class ColorSetting extends Setting {
    private int color;

    public ColorSetting(String name, int defaultColor) {
        super(name);
        this.color = defaultColor;
    }

    public int get() {
        return color;
    }

    public void set(int color) {
        this.color = color;
    }
}
