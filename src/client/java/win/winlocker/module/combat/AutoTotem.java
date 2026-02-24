package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

public class AutoTotem extends Module {
    private final SliderSetting health = new SliderSetting("Health", 3.5, 1.0, 20.0);

    public AutoTotem() {
        super("AutoTotem", Category.COMBAT);
        addSetting(health);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.getHealth() <= health.get()) {
            if (mc.player.getOffhandItem().getItem() != Items.TOTEM_OF_UNDYING) {
                int slot = findTotem(mc);
                if (slot != -1) {
                    mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, slot, 0, ClickType.PICKUP, mc.player);
                    mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, 45, 0, ClickType.PICKUP, mc.player);
                    mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    private int findTotem(Minecraft mc) {
        // Проверяем хотбар (инпуты 0-8 в инвентаре соответствуют 36-44 в контейнере инвентаря)
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i + 36;
            }
        }
        // Проверяем остальной инвентарь
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }
        return -1;
    }
}
