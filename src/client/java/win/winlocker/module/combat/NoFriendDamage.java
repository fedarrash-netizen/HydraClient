package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

public class NoFriendDamage extends Module {
    private final BooleanSetting preventDamage = new BooleanSetting("Prevent Damage", true);
    private final BooleanSetting preventKnockback = new BooleanSetting("Prevent Knockback", true);
    private final BooleanSetting preventFire = new BooleanSetting("Prevent Fire", true);
    private final BooleanSetting preventFall = new BooleanSetting("Prevent Fall", true);
    private final BooleanSetting preventDrown = new BooleanSetting("Prevent Drown", true);
    private final SliderSetting maxFriends = new SliderSetting("Max Friends", 5, 1, 20);
    
    public NoFriendDamage() {
        super("NoFriendDamage", Category.COMBAT);
        addSetting(preventDamage);
        addSetting(preventKnockback);
        addSetting(preventFire);
        addSetting(preventFall);
        addSetting(preventDrown);
        addSetting(maxFriends);
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        // Проверяем всех игроков в радиусе
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Проверяем что это не мы и игрок не в режиме спектатора
                if (!player.equals(mc.player) && !player.isSpectator()) {
                    // Проверяем что это друг (в хотбаре или в списке друзей)
                    if (isFriend(player)) {
                        // Предотвращаем урон от друга
                        if (preventDamage.get()) {
                            player.invulnerableTime = 1; // Делаем неуязвимым на 1 тик
                        }
                        
                        // Предотвращаем отбрасывание
                        if (preventKnockback.get()) {
                            player.setDeltaMovement(0, 0, 0);
                        }
                        
                        // Предотвращаем урон от огня
                        if (preventFire.get() && player.isOnFire()) {
                            player.clearFire();
                        }
                        
                        // Предотвращаем урон от падения
                        if (preventFall.get() && player.fallDistance > 0) {
                            player.fallDistance = 0;
                        }
                        
                        // Предотвращаем утопление
                        if (preventDrown.get() && player.isUnderWater()) {
                            player.setAirSupply(300); // Восстанавливаем воздух
                        }
                    }
                }
            }
        }
    }
    
    private boolean isFriend(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        
        // Проверяем список друзей (можно добавить в будущем)
        // TODO: Добавить список друзей
        
        return false;
    }
}
