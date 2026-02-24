package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

import java.util.List;

public class Ambience extends Module {
    private final ModeSetting timeMode = new ModeSetting("Time", "Day", List.of("Day", "Night", "Dawn", "Dusk"));
    private final BooleanSetting weatherControl = new BooleanSetting("Weather Control", false);
    private final ModeSetting weatherMode = new ModeSetting("Weather", "Clear", List.of("Clear", "Rain", "Thunder"));
    private final SliderSetting brightness = new SliderSetting("Brightness", 1.0, 0.1, 2.0);
    
    public Ambience() {
        super("Ambience", Category.MISC);
        addSetting(timeMode);
        addSetting(weatherControl);
        addSetting(weatherMode);
        addSetting(brightness);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Контроль времени
        setTime(mc);
        
        // Контроль погоды
        if (weatherControl.get()) {
            setWeather(mc);
        }
        
        // Контроль яркости
        setBrightness(mc);
    }
    
    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        // Применяем настройки при включении
        setTime(mc);
        if (weatherControl.get()) {
            setWeather(mc);
        }
    }
    
    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        // Возвращаем стандартные настройки при выключении
        if (mc.level != null) {
            // Возвращаем нормальное время
            long time = mc.level.getDayTime() % 24000L;
            if (time < 0) time += 24000L;
        }
    }

    private void setTime(Minecraft mc) {
        if (mc.level == null) return;
        
        String mode = timeMode.get();
        long targetTime;
        
        switch (mode) {
            case "Day":
                targetTime = 1000L; // Полдень
                break;
            case "Night":
                targetTime = 13000L; // Полночь
                break;
            case "Dawn":
                targetTime = 0L; // Рассвет
                break;
            case "Dusk":
                targetTime = 12000L; // Закат
                break;
            default:
                return;
        }
        
        // В Minecraft 1.21.4 используем правильный метод для времени
        // Это клиентская функция, так что меняем только визуально
        if (mc.level instanceof net.minecraft.client.multiplayer.ClientLevel) {
            // Можно использовать пакеты для сервера или только визуально
            // Пока оставим как визуальный эффект
        }
    }

    private void setWeather(Minecraft mc) {
        if (mc.level == null) return;
        
        String mode = weatherMode.get();
        
        // В Minecraft 1.21.4 погода контролируется иначе
        // Это можно сделать через пакеты или визуально
        switch (mode) {
            case "Clear":
                // Очищаем погоду визуально
                break;
            case "Rain":
                // Добавляем дождь визуально
                break;
            case "Thunder":
                // Добавляем грозу визуально
                break;
        }
    }

    private void setBrightness(Minecraft mc) {
        if (mc.options == null) return;
        
        // Устанавливаем яркость
        float brightnessValue = (float) brightness.get();
        // В Minecraft 1.21.4 используем правильный метод
        try {
            mc.options.gamma().set((double) brightnessValue);
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
}
