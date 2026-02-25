package win.winlocker.render.nametags;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import win.winlocker.utils.render.TextUtils;
import win.winlocker.utils.render.color.ColorUtils;
import win.winlocker.utils.render.DisplayUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Рендерер для NameTags (имена над игроками)
 */
public class NameTagsRenderer {
    private static NameTagsRenderer instance;
    private NameTagsConfig config;

    private NameTagsRenderer() {
        this.config = new NameTagsConfig();
    }

    public static NameTagsRenderer getInstance() {
        if (instance == null) {
            instance = new NameTagsRenderer();
        }
        return instance;
    }

    /**
     * Получить конфигурацию
     */
    public NameTagsConfig getConfig() {
        return config;
    }

    /**
     * Рендер всех NameTags
     */
    public void render(GuiGraphics graphics, PoseStack poseStack, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.font == null) {
            return;
        }

        if (!config.isEnabled()) {
            return;
        }

        // Получаем всех игроков и сортируем по расстоянию
        List<AbstractClientPlayer> players = mc.level.players().stream()
            .filter(p -> p != mc.player)
            .filter(p -> p.isAlive())
            .filter(p -> mc.player.distanceTo(p) <= config.getMaxRange())
            .sorted(Comparator.comparingDouble(p -> mc.player.distanceToSqr(p)))
            .collect(Collectors.toList());

        if (config.isSortByDistance()) {
            players.sort(Comparator.comparingDouble(p -> -mc.player.distanceToSqr(p)));
        }

        for (AbstractClientPlayer player : players) {
            renderNameTag(graphics, poseStack, player, partialTicks);
        }
    }

    /**
     * Рендер NameTag игрока
     */
    private void renderNameTag(GuiGraphics graphics, PoseStack poseStack, 
                               AbstractClientPlayer player, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null) {
            return;
        }

        // Позиция игрока с интерполяцией
        double x = player.xo + (player.getX() - player.xo) * partialTicks;
        double y = player.yo + (player.getY() - player.yo) * partialTicks;
        double z = player.zo + (player.getZ() - player.zo) * partialTicks;

        // Позиция камеры
        double camX = mc.cameraEntity.xo + (mc.cameraEntity.getX() - mc.cameraEntity.xo) * partialTicks;
        double camY = mc.cameraEntity.yo + (mc.cameraEntity.getY() - mc.cameraEntity.yo) * partialTicks;
        double camZ = mc.cameraEntity.zo + (mc.cameraEntity.getZ() - mc.cameraEntity.zo) * partialTicks;

        // Относительная позиция
        double relX = x - camX;
        double relY = y - camY + player.getBbHeight() + 0.5;
        double relZ = z - camZ;

        // Проверяем, находится ли игрок за камерой
        if (relZ >= 0) {
            return;
        }

        // Проекция на экран
        float[] screenPos = projectToScreen(relX, relY, relZ);
        if (screenPos == null) {
            return;
        }

        float screenX = screenPos[0];
        float screenY = screenPos[1];

        // Получаем имя
        String name = getPlayerName(player);
        float nameWidth = TextUtils.width(mc.font, name);
        float nameHeight = mc.font.lineHeight;

        // Получаем дополнительную информацию
        String info = getPlayerInfo(player);
        float infoWidth = info.isEmpty() ? 0 : TextUtils.width(mc.font, info);

        // Размеры бокса
        float boxWidth = Math.max(nameWidth, infoWidth) + config.getPaddingX() * 2;
        float boxHeight = nameHeight + (info.isEmpty() ? 0 : nameHeight + 2) + config.getPaddingY() * 2;

        // Позиция бокса
        float boxX = screenX - boxWidth / 2;
        float boxY = screenY - boxHeight / 2;

        // Рендер фона
        int bgColor = config.getBackgroundColor();
        if (config.isShadowEnabled()) {
            DisplayUtils.drawRoundedRect(graphics, boxX - 2, boxY - 2, boxWidth + 4, boxHeight + 4,
                                         config.getShadowRadius(), ColorUtils.setAlpha(0x000000, 50));
        }

        if (config.isRoundedCorners()) {
            DisplayUtils.drawRoundedRect(graphics, boxX, boxY, boxWidth, boxHeight,
                                         config.getCornerRadius(), bgColor);
        } else {
            DisplayUtils.drawRect(graphics, boxX, boxY, boxWidth, boxHeight, bgColor);
        }

        // Рендер рамки
        if (config.getBorderWidth() > 0) {
            DisplayUtils.drawBorder(graphics, boxX, boxY, boxWidth, boxHeight,
                                    config.getBorderWidth(), config.getBorderColor());
        }

        // Рендер имени
        int nameColor = getNameColor(player);
        float nameY = boxY + config.getPaddingY();
        
        if (config.isCentered()) {
            TextUtils.drawCentered(graphics, mc.font, name, screenX, nameY, nameColor, config.isTextShadow());
        } else {
            TextUtils.draw(graphics, mc.font, name, boxX + config.getPaddingX(), nameY, nameColor, config.isTextShadow());
        }

        // Рендер дополнительной информации
        if (!info.isEmpty()) {
            int infoColor = config.getInfoColor();
            float infoY = nameY + nameHeight + 2;
            
            if (config.isCentered()) {
                TextUtils.drawCentered(graphics, mc.font, info, screenX, infoY, infoColor, config.isTextShadow());
            } else {
                TextUtils.draw(graphics, mc.font, info, boxX + config.getPaddingX(), infoY, infoColor, config.isTextShadow());
            }
        }

        // Рендер иконки друга
        if (config.isShowFriendIcon() && isFriend(player)) {
            String friendIcon = "♥";
            float iconX = boxX + boxWidth + 2;
            float iconY = boxY + config.getPaddingY();
            TextUtils.draw(graphics, mc.font, friendIcon, iconX, iconY, config.getFriendColor(), false);
        }

        // Рендер дистанции
        if (config.isShowDistance()) {
            float distance = mc.player.distanceTo(player);
            String distanceText = String.format("%.1fm", distance);
            float distanceY = boxY + boxHeight + 2;
            TextUtils.drawCentered(graphics, mc.font, distanceText, screenX, distanceY,
                                   config.getDistanceColor(), false);
        }
    }

    /**
     * Получить имя игрока
     */
    private String getPlayerName(AbstractClientPlayer player) {
        String name = player.getName().getString();
        
        if (config.isHideOwnName() && player == Minecraft.getInstance().player) {
            return config.getOwnNameReplacement();
        }
        
        if (config.isShowPrefix() && config.getPrefix() != null) {
            return config.getPrefix() + name;
        }
        
        return name;
    }

    /**
     * Получить дополнительную информацию об игроке
     */
    private String getPlayerInfo(AbstractClientPlayer player) {
        if (!config.isShowInfo()) {
            return "";
        }

        StringBuilder info = new StringBuilder();

        if (config.isShowHealth()) {
            float healthPercent = player.getHealth() / player.getMaxHealth();
            info.append(String.format("❤ %.1f", player.getHealth()));
        }

        if (config.isShowArmor()) {
            int armor = player.getArmorValue();
            if (armor > 0) {
                if (info.length() > 0) info.append("  ");
                info.append("✛ ").append(armor);
            }
        }

        if (config.isShowItem()) {
            var item = player.getMainHandItem();
            if (!item.isEmpty()) {
                if (info.length() > 0) info.append("  ");
                info.append(item.getDisplayName().getString());
            }
        }

        return info.toString();
    }

    /**
     * Получить цвет имени
     */
    private int getNameColor(AbstractClientPlayer player) {
        if (isFriend(player)) {
            return config.getFriendColor();
        }

        if (config.isColorByTeam()) {
            var team = player.getTeam();
            if (team != null) {
                return team.getColor().getColor() != null ? 
                       team.getColor().getColor() : config.getNameColor();
            }
        }

        return config.getNameColor();
    }

    /**
     * Проверить, является ли игрок другом
     */
    private boolean isFriend(AbstractClientPlayer player) {
        // Здесь можно интегрировать с вашей системой друзей
        return false;
    }

    /**
     * Проекция 3D координат в 2D экран
     */
    private float[] projectToScreen(double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null) {
            return null;
        }

        // Проверка на нахождение перед камерой
        if (z >= -0.1) {
            return null;
        }

        // Упрощенная проекция
        float fov = mc.options.fov().get().floatValue();
        float aspectRatio = (float) mc.getWindow().getWidth() / mc.getWindow().getHeight();
        
        float tanFov = (float) Math.tan(Math.toRadians(fov / 2));
        float projX = (float) (x / (-z * tanFov * aspectRatio));
        float projY = (float) (y / (-z * tanFov));

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        float screenX = (screenWidth / 2.0f) * (1 + projX);
        float screenY = (screenHeight / 2.0f) * (1 - projY);

        return new float[]{screenX, screenY};
    }
}
