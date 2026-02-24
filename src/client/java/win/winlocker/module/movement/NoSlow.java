package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.module.Module;

import java.util.Arrays;

public class NoSlow extends Module {
    public static final NoSlow INSTANCE = new NoSlow();
    
    private final ModeSetting mode = new ModeSetting("Режим", "Legit", 
            Arrays.asList("Legit", "Vanilla"));
    private int calls = 0;
    
    public NoSlow() {
        super("NoSlow", Module.Category.MOVEMENT);
        
        addSetting(mode);
        addSetting(new KeyBindSetting("Bind", 0));
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        if (mode.get().equals("Legit") && mc.player.isUsingItem()) {
            calls++;
        } else {
            calls = 0;
        }
        
        // Основная логика NoSlow - предотвращаем замедление при использовании предметов
        if (mc.player.isUsingItem()) {
            // Устанавливаем скорость движения как будто не используем предмет
            mc.player.setSprinting(true);
        }
    }
    
    public void onItemUse(Object e) {
        if (mode.get().equals("Legit")) {
            if (calls % 5 == 0) {
                // Заглушка для события - логика будет добавлена позже
            }
        }
    }
    
    public void update(Object tickEvent) {
        if (mode.get().equals("Legit") && Minecraft.getInstance().player != null && Minecraft.getInstance().player.isUsingItem()) {
            calls++;
        } else {
            calls = 0;
        }
    }
}
