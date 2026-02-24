package win.winlocker.DropDown.settings;

public class ColorSetting extends Setting {
    private int color;
    private final int defaultColor;

    public ColorSetting(String name, int defaultColor) {
        super(name);
        this.color = defaultColor;
        this.defaultColor = defaultColor;
    }

    public int get() {
        return color;
    }

    public void set(int color) {
        this.color = color;
    }

    public void reset() {
        this.color = defaultColor;
    }

    public int getDefault() {
        return defaultColor;
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return color & 0xFF;
    }

    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    public void setRGB(int r, int g, int b) {
        int a = getAlpha();
        this.color = (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void setRGBA(int r, int g, int b, int a) {
        this.color = (a << 24) | (r << 16) | (g << 8) | b;
    }

    public float getHue() {
        return rgbToHsb(color)[0];
    }

    public float getSaturation() {
        return rgbToHsb(color)[1];
    }

    public float getBrightness() {
        return rgbToHsb(color)[2];
    }

    public void setHSB(float h, float s, float b) {
        int rgb = java.awt.Color.HSBtoRGB(h, s, b);
        int a = getAlpha();
        this.color = (a << 24) | (rgb & 0xFFFFFF);
    }

    private float[] rgbToHsb(int rgb) {
        float r = (float) ((rgb >> 16) & 0xFF) / 255.0f;
        float g = (float) ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (float) (rgb & 0xFF) / 255.0f;
        return java.awt.Color.RGBtoHSB((int) (r * 255), (int) (g * 255), (int) (b * 255), null);
    }
}
