package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;

public class NoHungry extends Module {
    
    private final ModeSetting mode = new ModeSetting("Режим", "Infinite", 
            java.util.List.of("Infinite", "Regeneration", "NoLoss"));
    private final BooleanSetting saturation = new BooleanSetting("Насыщение", true);
    private final SliderSetting regenerationSpeed = new SliderSetting("Скорость регенерации", 1.0, 0.1, 5.0);
    private final BooleanSetting visualEffects = new BooleanSetting("Визуальные эффекты", false);
    
    private long lastRegenTime = 0;
    
    public NoHungry() {
        super("NoHungry", Module.Category.MISC);
        
        addSetting(mode);
        addSetting(saturation);
        addSetting(regenerationSpeed);
        addSetting(visualEffects);
    }
    
    @Override
    public void onEnable() {
        lastRegenTime = 0;
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        Player player = mc.player;
        FoodData foodData = player.getFoodData();
        
        switch (mode.get()) {
            case "Infinite":
                handleInfinite(player, foodData);
                break;
            case "Regeneration":
                handleRegeneration(player, foodData);
                break;
            case "NoLoss":
                handleNoLoss(player, foodData);
                break;
        }
    }
    
    private void handleInfinite(Player player, FoodData foodData) {
        // Бесконечная еда и насыщение
        try {
            // Устанавливаем максимальные значения
            foodData.setFoodLevel(20);
            if (saturation.get()) {
                foodData.setSaturation(20.0f);
            }
            
            // Убираем негативные эффекты от голода
            if (player.hasEffect(MobEffects.HUNGER)) {
                player.removeEffect(MobEffects.HUNGER);
            }
            
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    private void handleRegeneration(Player player, FoodData foodData) {
        // Регенерация еды
        if (System.currentTimeMillis() - lastRegenTime < (long) (1000 / regenerationSpeed.get())) {
            return;
        }
        
        try {
            if (foodData.getFoodLevel() < 20) {
                foodData.setFoodLevel(foodData.getFoodLevel() + 1);
                
                if (saturation.get() && foodData.getSaturationLevel() < 20.0f) {
                    foodData.setSaturation(foodData.getSaturationLevel() + 1.0f);
                }
                
                lastRegenTime = System.currentTimeMillis();
            }
            
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    private void handleNoLoss(Player player, FoodData foodData) {
        // Без потерь еды
        try {
            // Сохраняем текущие значения
            float currentFood = foodData.getFoodLevel();
            float currentSaturation = foodData.getSaturationLevel();
            
            // Восстанавливаем если уменьшились
            if (currentFood < 20) {
                foodData.setFoodLevel(20);
            }
            
            if (saturation.get() && currentSaturation < 20.0f) {
                foodData.setSaturation(20.0f);
            }
            
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
    
    public boolean isHungry() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getFoodData().getFoodLevel() < 18;
    }
    
    public float getFoodLevel() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null ? mc.player.getFoodData().getFoodLevel() : 0;
    }
    
    public float getSaturationLevel() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null ? mc.player.getFoodData().getSaturationLevel() : 0;
    }
}
