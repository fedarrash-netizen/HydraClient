package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.module.Module;

public class InventoryMove extends Module {
    public InventoryMove() {
        super("InventoryMove", Category.MOVEMENT);
        addSetting(new KeyBindSetting("Bind", 0));
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // Работаем только когда открыт инвентарь
        if (mc.screen instanceof InventoryScreen) {
            // Разрешаем движение в инвентаре
            // В Minecraft 1.21.4 движение в инвентаре уже частично разрешено
            // но можно добавить дополнительную логику если нужно
            
            // Устанавливаем движение вперёд/назад/влево/вправо
            if (mc.options.keyUp.isDown()) {
                mc.player.setSprinting(true);
            }
        }
    }
}
