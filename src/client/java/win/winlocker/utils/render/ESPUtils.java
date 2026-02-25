package win.winlocker.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import win.winlocker.utils.render.color.ColorUtils;

/**
 * Простые утилиты для ESP и NameTags
 */
public class ESPUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    
    // Настройки ESP
    public static boolean enableESP = false;
    public static boolean espBox = true;
    public static boolean espHealth = true;
    public static int espBoxColor = ColorUtils.rgba(0, 255, 0, 150);
    public static float espRange = 100f;
    
    // Настройки NameTags
    public static boolean enableNameTags = false;
    public static boolean nameTagsHealth = true;
    public static boolean nameTagsArmor = true;
    public static boolean nameTagsDistance = false;
    public static int nameTagsBgColor = ColorUtils.rgba(0, 0, 0, 150);
    public static int nameTagsTextColor = ColorUtils.rgba(255, 255, 255, 255);
    public static float nameTagsScale = 1f;

    /**
     * Рендер ESP и NameTags для всех игроков
     */
    public static void render(GuiGraphics graphics) {
        if (mc.level == null || mc.player == null || mc.font == null) {
            return;
        }

        for (AbstractClientPlayer player : mc.level.players()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }

            float distance = mc.player.distanceTo(player);
            if (distance > espRange) {
                continue;
            }

            // Рендер ESP
            if (enableESP) {
                renderESP(graphics, player, distance);
            }

            // Рендер NameTags
            if (enableNameTags) {
                renderNameTag(graphics, player, distance);
            }
        }
    }

    /**
     * Рендер ESP бокса
     */
    private static void renderESP(GuiGraphics graphics, Player player, float distance) {
        // Упрощённый ESP - только цветной бокс вокруг имени
        // Для полноценного 3D ESP нужен доступ к PoseStack
    }

    /**
     * Рендер NameTag
     */
    private static void renderNameTag(GuiGraphics graphics, Player player, float distance) {
        // Проекция 3D координат в 2D
        double x = player.getX() - mc.cameraEntity.getX();
        double y = player.getY() - mc.cameraEntity.getY() + player.getBbHeight() + 0.5;
        double z = player.getZ() - mc.cameraEntity.getZ();

        if (z >= 0) {
            return; // За камерой
        }

        // Простая проекция
        float fov = mc.options.fov().get().floatValue();
        float aspectRatio = (float) mc.getWindow().getWidth() / mc.getWindow().getHeight();
        float tanFov = (float) Math.tan(Math.toRadians(fov / 2));
        
        float projX = (float) (x / (-z * tanFov * aspectRatio));
        float projY = (float) (y / (-z * tanFov));

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float screenX = (screenWidth / 2.0f) * (1 + projX);
        float screenY = (screenHeight / 2.0f) * (1 - projY);

        // Текст
        String name = player.getName().getString();
        
        // Дополнительная информация
        StringBuilder info = new StringBuilder();
        if (nameTagsHealth) {
            info.append(String.format("❤ %.1f", player.getHealth()));
        }
        if (nameTagsArmor) {
            int armor = player.getArmorValue();
            if (armor > 0) {
                if (info.length() > 0) info.append("  ");
                info.append("✛ ").append(armor);
            }
        }
        if (nameTagsDistance) {
            if (info.length() > 0) info.append("  ");
            info.append(String.format("%.1fm", distance));
        }

        String fullText = name + (info.length() > 0 ? " §7" + info.toString() : "");
        int textWidth = mc.font.width(fullText);
        int textHeight = mc.font.lineHeight;

        // Фон
        int bgX = Math.round(screenX - textWidth / 2f - 2f);
        int bgY = Math.round(screenY - 2f);
        int bgWidth = textWidth + 4;
        int bgHeight = textHeight + 2;

        graphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, nameTagsBgColor);

        // Текст
        graphics.drawString(mc.font, fullText, 
            Math.round(screenX - textWidth / 2f), 
            Math.round(screenY), 
            nameTagsTextColor, true);
    }

    /**
     * Включить/выключить ESP
     */
    public static void toggleESP() {
        enableESP = !enableESP;
    }

    /**
     * Включить/выключить NameTags
     */
    public static void toggleNameTags() {
        enableNameTags = !enableNameTags;
    }

    /**
     * Сбросить настройки
     */
    public static void reset() {
        enableESP = false;
        enableNameTags = false;
    }
}
