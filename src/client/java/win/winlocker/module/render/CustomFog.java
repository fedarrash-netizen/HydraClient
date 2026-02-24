package win.winlocker.module.render;

import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.module.Module;

import java.util.List;

public class CustomFog extends Module {
    private static CustomFog instance;

    public final ModeSetting mode = new ModeSetting("Мод", "Клиент", List.of("Клиент", "Свой"));
    public final ColorSetting color = new ColorSetting("Цвет", 0xFF5AA8FF);

    public CustomFog() {
        super("CustomFog", Category.RENDER);
        instance = this;
        addSetting(mode);
        addSetting(color);
    }

    public static CustomFog getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public boolean isClientMode() {
        return isEnabled() && "Клиент".equals(mode.get());
    }

    public boolean isCustomMode() {
        return isEnabled() && "Свой".equals(mode.get());
    }

    public int getColor() {
        return color.get();
    }

    public float[] getColorRGB() {
        int c = getColor();
        float r = (float) ((c >> 16) & 0xFF) / 255.0f;
        float g = (float) ((c >> 8) & 0xFF) / 255.0f;
        float b = (float) (c & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }
}
