package win.winlocker.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;

public class KeepSprint extends Module {
    
    private final BooleanSetting stopInWater = new BooleanSetting("Останавливать в воде", false);
    private final BooleanSetting stopWhenHungry = new BooleanSetting("Останавливать когда голоден", false);
    private final BooleanSetting onlyInCombat = new BooleanSetting("Только в бою", false);
    private final BooleanSetting ignoreBlindness = new BooleanSetting("Игнорировать слепоту", true);
    
    private boolean wasSprinting = false;
    
    public KeepSprint() {
        super("KeepSprint", Module.Category.MOVEMENT);
        
        addSetting(stopInWater);
        addSetting(stopWhenHungry);
        addSetting(onlyInCombat);
        addSetting(ignoreBlindness);
    }
    
    @Override
    public void onEnable() {
        wasSprinting = false;
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        Player player = mc.player;
        
        // Сохраняем текущее состояние спринта
        boolean isSprinting = player.isSprinting();
        
        if (isSprinting && !wasSprinting) {
            // Игрок только что начал спринтить
            wasSprinting = true;
        } else if (!isSprinting && wasSprinting && shouldKeepSprint(player)) {
            // Игрок перестал спринтить, но должен продолжать
            player.setSprinting(true);
            wasSprinting = true;
        } else {
            // Обновляем состояние
            wasSprinting = isSprinting && shouldKeepSprint(player) && player.zza > 0;
        }
        
    }
    
    private boolean shouldKeepSprint(Player player) {
        // Проверяем условия для сохранения спринта
        
        // Остановка в воде
        if (stopInWater.get() && player.isInWater()) {
            return false;
        }
        
        // Остановка когда голоден
        if (stopWhenHungry.get() && player.getFoodData().getFoodLevel() <= 6) {
            return false;
        }
        
        // Только в бою
        if (onlyInCombat.get() && !isInCombat(player)) {
            return false;
        }
        
        // Проверка на эффекты
        if (player.hasEffect(MobEffects.BLINDNESS) && !ignoreBlindness.get()) {
            return false;
        }

        
        // Не должен быть в режиме невидимости
        if (player.hasEffect(MobEffects.INVISIBILITY)) {
            return false;
        }
        
        return true;
    }
    
    private boolean isInCombat(Player player) {
        // Проверяем в бою ли игрок
        return player.hurtTime > 0 || player.getLastHurtMobTimestamp() > 0;
    }
    
    public boolean isKeepingSprint() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.isSprinting() && shouldKeepSprint(mc.player);
    }
    
    public void forceSprint() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && shouldKeepSprint(mc.player)) {
            mc.player.setSprinting(true);
            wasSprinting = true;
        }
    }
}
