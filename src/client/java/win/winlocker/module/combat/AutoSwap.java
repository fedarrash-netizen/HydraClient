package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.InteractionHand;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;

import java.util.List;

public class AutoSwap extends Module {
    // Настройки
    private final ModeSetting mode = new ModeSetting("Mode", "Auto", List.of("Auto", "Combat", "Utility", "Totem->Head", "Head->Totem"));
    private final KeyBindSetting autoBind = new KeyBindSetting("Auto Mode", 0);
    private final KeyBindSetting combatBind = new KeyBindSetting("Combat Mode", 0);
    private final KeyBindSetting utilityBind = new KeyBindSetting("Utility Mode", 0);
    
    // Порог здоровья для автосwap
    private float healthThreshold = 7.0f;
    private int delayTicks = 4;
    private int cooldown = 0;
    
    // Предметы для свапа
    private Item lowHealthItem = Items.GOLDEN_APPLE;
    private Item combatItem = Items.NETHERITE_SWORD;
    private Item utilityItem = Items.TOTEM_OF_UNDYING;
    
    private String currentMode = "Auto";
    private boolean lastAutoState = false;
    private boolean lastCombatState = false;
    private boolean lastUtilityState = false;

    public AutoSwap() {
        super("AutoSwap", Category.COMBAT);
        addSetting(mode);
        addSetting(autoBind);
        addSetting(combatBind);
        addSetting(utilityBind);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Обработка кнопок
        handleKeyBinds(mc);
        
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        // Выполнение логики в зависимости от режима
        switch (currentMode) {
            case "Auto":
                autoLogic(mc.player);
                break;
            case "Combat":
                forceItem(mc.player, combatItem);
                break;
            case "Utility":
                forceItem(mc.player, utilityItem);
                break;
            case "Totem->Head":
                swapTotemToHead(mc);
                break;
            case "Head->Totem":
                swapHeadToTotem(mc);
                break;
        }
    }
    
    private void handleKeyBinds(Minecraft mc) {
        // Проверяем нажатия кнопок
        boolean currentAutoState = isKeyPressed(autoBind.getKey());
        boolean currentCombatState = isKeyPressed(combatBind.getKey());
        boolean currentUtilityState = isKeyPressed(utilityBind.getKey());
        
        if (currentAutoState && !lastAutoState) {
            currentMode = "Auto";
        }
        if (currentCombatState && !lastCombatState) {
            currentMode = "Combat";
        }
        if (currentUtilityState && !lastUtilityState) {
            currentMode = "Utility";
        }
        
        lastAutoState = currentAutoState;
        lastCombatState = currentCombatState;
        lastUtilityState = currentUtilityState;
        
        // Обновляем режим из настроек
        currentMode = mode.get();
    }
    
    private boolean isKeyPressed(int key) {
        if (key == 0) return false;
        return Minecraft.getInstance().options.keyAttack.isDown();
    }

    private void autoLogic(Player player) {
        // Если здоровье низкое, используем предмет для лечения
        if (player.getHealth() <= healthThreshold) {
            if (forceItem(player, lowHealthItem)) return;
        }
        // Иначе используем боевой предмет
        forceItem(player, combatItem);
    }

    private boolean forceItem(Player player, Item item) {
        int slot = findHotbarItem(player, item);
        if (slot == -1) return false;
        
        if (player.getInventory().selected != slot) {
            player.getInventory().selected = slot;
            player.swing(InteractionHand.MAIN_HAND);
            cooldown = delayTicks;
            return true;
        }
        return false;
    }

    private void swapTotemToHead(Minecraft mc) {
        if (mc.player.getInventory() == null) return;
        
        // Ищем тотем в инвентаре
        int totemSlot = findItem(mc.player, Items.TOTEM_OF_UNDYING);
        if (totemSlot == -1) return;
        
        // Ищем голову в хотбаре
        int headSlot = findHotbarItem(mc.player, Items.PLAYER_HEAD);
        
        if (headSlot != -1) {
            // Меняем тотем на голову
            mc.player.getInventory().selected = totemSlot;
            cooldown = delayTicks;
        }
    }

    private void swapHeadToTotem(Minecraft mc) {
        if (mc.player.getInventory() == null) return;
        
        // Ищем тотем в инвентаре
        int totemSlot = findItem(mc.player, Items.TOTEM_OF_UNDYING);
        if (totemSlot == -1) return;
        
        // Ищем голову в хотбаре
        int headSlot = findHotbarItem(mc.player, Items.PLAYER_HEAD);
        
        if (headSlot != -1) {
            // Меняем голову на тотем
            mc.player.getInventory().selected = totemSlot;
            cooldown = delayTicks;
        }
    }

    private int findHotbarItem(Player player, Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
    
    private int findItem(Player player, Item item) {
        for (int i = 0; i < 36; i++) { // 36 = hotbar + main inventory
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}
