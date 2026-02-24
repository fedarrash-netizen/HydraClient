package win.winlocker.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

public class NameTags extends Module {
    private final SliderSetting scale = new SliderSetting("Scale", 1.0, 0.5, 3.0);
    
    public NameTags() {
        super("NameTags", Category.RENDER);
        addSetting(scale);
    }

    public void onRender() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        float scaleValue = (float) scale.get();
        
        // Рендерим имена всех сущностей
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                
                // Получаем имя сущности
                String name = living.getName().getString();
                if (name == null || name.isEmpty()) {
                    name = "Unknown";
                }
                
                // Просто выводим имя в чат для теста
                if (mc.player != null) {
                    mc.player.setCustomName(Component.literal("§7" + name + " §7[Scale: " + scaleValue + "]"));
                }
            }
        }
    }
}
