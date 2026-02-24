package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.module.Module;

public class GuiMove extends Module {
    public GuiMove() {
        super("GuiMove", Category.MOVEMENT);
        addSetting(new KeyBindSetting("Bind", 0));
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen == null) return;

        // В Fabric/Vanilla движение в GUI заблокировано в KeyboardInput
        // Этот модуль обычно работает через миксин в KeyboardInput
    }
}
