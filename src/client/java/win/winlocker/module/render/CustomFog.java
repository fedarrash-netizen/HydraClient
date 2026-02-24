package win.winlocker.module.render;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.module.Module;

import java.util.List;

public class CustomFog extends Module {
    private static CustomFog instance;

    public final SliderSetting power = new SliderSetting("Сила", 20.0, 1.0, 100.0);
    public final ModeSetting mode = new ModeSetting("Мод", "Клиент", List.of("Клиент", "Свой"));
    public final ColorSetting color = new ColorSetting("Цвет", -1);

    public CustomFog() {
        super("CustomFog", Category.RENDER);
        instance = this;
        addSetting(power);
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

    public double getPower() {
        return power.get();
    }

    public int getColor() {
        return color.get();
    }
}
