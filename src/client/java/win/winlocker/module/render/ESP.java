package win.winlocker.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.module.Module;

public class ESP extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", true);
    private final BooleanSetting items = new BooleanSetting("Items", false);
    private final BooleanSetting invisibles = new BooleanSetting("Invisibles", true);
    
    private final SliderSetting renderDistance = new SliderSetting("Distance", 100, 50, 200);
    private final ColorSetting playerColor = new ColorSetting("Player Color", 0xFF5AA8FF);
    private final ColorSetting mobColor = new ColorSetting("Mob Color", 0xFF00FF00);
    private final ColorSetting monsterColor = new ColorSetting("Monster Color", 0xFFFF0000);
    private final ColorSetting itemColor = new ColorSetting("Item Color", 0xFFFFFF00);
    private final BooleanSetting healthBar = new BooleanSetting("Health Bar", true);
    private final BooleanSetting nameTags = new BooleanSetting("Name Tags", true);
    private final SliderSetting nameSize = new SliderSetting("Name Size", 1.0, 0.5, 2.0);

    public ESP() {
        super("ESP", Category.RENDER);
        addSetting(players);
        addSetting(mobs);
        addSetting(monsters);
        addSetting(items);
        addSetting(invisibles);
        addSetting(renderDistance);
        addSetting(playerColor);
        addSetting(mobColor);
        addSetting(monsterColor);
        addSetting(itemColor);
        addSetting(healthBar);
        addSetting(nameTags);
        addSetting(nameSize);
    }

    public void onRender() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        float distance = (float) renderDistance.get();
        float nameScale = (float) nameSize.get();
        
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) continue;
            
            double dist = mc.player.distanceTo(entity);
            if (dist > distance) continue;
            
            // Проверяем тип сущности и настройки
            if (entity instanceof Player && players.get()) {
                renderEntity(entity, playerColor.get(), dist, nameScale, true);
            } else if (entity instanceof Animal && mobs.get()) {
                renderEntity(entity, mobColor.get(), dist, nameScale, false);
            } else if (entity instanceof Monster && monsters.get()) {
                renderEntity(entity, monsterColor.get(), dist, nameScale, false);
            } else if (entity instanceof ItemEntity && items.get()) {
                renderItem(entity, itemColor.get(), dist);
            } else if (invisibles.get() && entity.isInvisible()) {
                renderEntity(entity, 0xFFFFFF00, dist, nameScale, true);
            }
        }
    }

    private void renderEntity(Entity entity, int color, double distance, float nameScale, boolean isPlayer) {
        Minecraft mc = Minecraft.getInstance();
        
        // Рендерим bounding box
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            float width = living.getBbWidth();
            float height = living.getBbHeight();
            
            double x = living.getX() - width / 2;
            double y = living.getY() - height / 2;
            double z = living.getZ() - width / 2;
            
            // Рендерим линии бокса
            renderOutlineBox(x, y, z, width, height, color);
            
            // Рендерим полоску здоровья если включено
            if (healthBar.get() && isPlayer) {
                renderHealthBar(living, x, y, width, color);
            }
        }
        
        // Рендерим имя если включено
        if (nameTags.get()) {
            renderNameTag(entity, color, nameScale);
        }
    }

    private void renderItem(Entity entity, int color, double distance) {
        if (!(entity instanceof ItemEntity)) return;
        
        ItemEntity item = (ItemEntity) entity;
        float size = 0.5f;
        
        double x = item.getX() - size / 2;
        double y = item.getY() - size / 2;
        double z = item.getZ() - size / 2;
        
        // Рендерим bounding box для предмета
        renderOutlineBox(x, y, z, size, size, color);
    }

    private void renderOutlineBox(double x, double y, double z, float width, float height, int color) {
        Minecraft mc = Minecraft.getInstance();
        
        // Здесь можно добавить рендеринг линий бокса
        // В Minecraft 1.21.4 это делается через VertexBuffer или другие методы
        
        // Простая реализация - рендерим линии по углам
        double[][] corners = {
            {x, y, z},
            {x + width, y, z},
            {x + width, y + height, z},
            {x, y + height, z},
            {x, y, z + width},
            {x + width, y, z + width},
            {x + width, y + height, z + width},
            {x, y + height, z + width}
        };
        
        // Рендерим линии между углами (упрощённая версия)
        for (int i = 0; i < 4; i++) {
            int start = i * 2;
            int end = start + 2;
            if (end >= corners.length) end = 0;
            
            // Здесь можно добавить рендеринг линий
        }
    }

    private void renderHealthBar(LivingEntity entity, double x, double y, float width, int color) {
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        
        if (health <= 0 || maxHealth <= 0) return;
        
        float healthPercent = health / maxHealth;
        float barWidth = width * 0.8f;
        float barHeight = 2f;
        float barY = (float)(y - 5);
        
        // Фон полоски здоровья
        int bgColor = 0xFF000000;
        int healthColor = healthPercent > 0.5f ? 0xFF00FF00 : 0xFFFF0000;
        
        // Здесь можно добавить рендеринг полоски здоровья
    }

    private void renderNameTag(Entity entity, int color, float scale) {
        if (entity.hasCustomName()) {
            String name = entity.getName().getString();
            
            // Рендерим имя над сущностью
            // В Minecraft 1.21.4 это делается через Font
            
            // Простая реализация
            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight() + 0.5;
            double z = entity.getZ();
            
            // Здесь можно добавить рендеринг текста
        }
    }
}
