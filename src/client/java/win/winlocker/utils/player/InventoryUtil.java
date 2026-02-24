package win.winlocker.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;

public class InventoryUtil {
    
    public static int getBestWeaponSlot() {
        if (Minecraft.getInstance().player == null) {
            return -1;
        }
        
        int bestSlot = -1;
        double bestDamage = 0;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
            if (stack.getItem() instanceof SwordItem) {
                double damage = stack.getDamageValue();
                
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = i;
                }
            }
        }
        
        return bestSlot;
    }
    
    public static int getAxeSlot() {
        if (Minecraft.getInstance().player == null) {
            return -1;
        }
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
            if (stack.getItem() instanceof AxeItem) {
                return i;
            }
        }
        
        return -1;
    }
    
    public static int getSlotInHotbar(ItemStack item) {
        if (Minecraft.getInstance().player == null) {
            return -1;
        }
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
            if (stack.getItem() == item.getItem()) {
                return i;
            }
        }
        
        return -1;
    }
    
    public static boolean hasItemInHotbar(ItemStack item) {
        return getSlotInHotbar(item) != -1;
    }
    
    public static int getItemCount(ItemStack item) {
        if (Minecraft.getInstance().player == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < Minecraft.getInstance().player.getInventory().getContainerSize(); i++) {
            ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(i);
            if (stack.getItem() == item.getItem()) {
                count += stack.getCount();
            }
        }
        
        return count;
    }
}
