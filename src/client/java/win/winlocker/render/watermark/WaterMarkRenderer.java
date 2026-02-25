package win.winlocker.render.watermark;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import win.winlocker.utils.render.DisplayUtils;
import win.winlocker.utils.render.TextUtils;
import win.winlocker.utils.render.LogoRenderer;
import win.winlocker.utils.render.Animation;
import win.winlocker.utils.render.AnimationUtils;

/**
 * Рендерер WaterMark'а для клиента
 * Не зависит от TLoaderClient, использует только утилиты
 */
public class WaterMarkRenderer {
    private static WaterMarkRenderer instance;

    private final WaterMarkConfig config;
    private final Animation fadeAnimation;
    private final Animation scaleAnimation;
    private long enableTime;
    private boolean enabled;

    private WaterMarkRenderer() {
        this.config = new WaterMarkConfig();
        this.fadeAnimation = new Animation(0f, 300, AnimationUtils.EASE_OUT_QUAD);
        this.scaleAnimation = new Animation(1f, 300, AnimationUtils.EASE_OUT_BACK);
        this.enableTime = 0;
        this.enabled = false;
    }

    public static WaterMarkRenderer getInstance() {
        if (instance == null) {
            instance = new WaterMarkRenderer();
        }
        return instance;
    }

    /**
     * Получить конфигурацию
     */
    public WaterMarkConfig getConfig() {
        return config;
    }

    /**
     * Включить/выключить WaterMark
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.enableTime = System.currentTimeMillis();
            this.fadeAnimation.setTarget(1f, 300, AnimationUtils.EASE_OUT_QUAD);
            this.scaleAnimation.setTarget(1f, 300, AnimationUtils.EASE_OUT_BACK);
        } else {
            this.fadeAnimation.setTarget(0f, 200, AnimationUtils.EASE_IN_QUAD);
        }
    }

    /**
     * Проверить, включен ли WaterMark
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Обновить анимации
     */
    public void update() {
        if (enabled) {
            fadeAnimation.update();
            scaleAnimation.update();
        }
    }

    /**
     * Рендер WaterMark'а
     */
    public void render(GuiGraphics graphics) {
        if (!enabled || !config.isEnabled()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.font == null) {
            return;
        }

        update();

        float alpha = fadeAnimation.getValue();
        if (alpha <= 0.01f) {
            return;
        }

        // Позиция WaterMark'а из конфига
        float x = config.getPosX();
        float y = config.getPosY();
        float scale = config.getScale();
        float height = 30f * scale;
        float radius = 4f * scale;
        float logoSize = 23f * scale;

        float paddingLeft = 5f * scale;
        float logoPadding = 2f * scale;
        float paddingRight = 4f * scale;
        float statsPadding = 8f * scale;

        String title = config.getCustomTitle();
        String subtitle = config.isShowUid() ? config.getCustomSubtitle() : "";

        float titleSize = 7f * scale;
        float subSize = 5f * scale;
        float statsSize = 5f * scale;

        float titleWidth = TextUtils.width(mc.font, title);
        float subWidth = TextUtils.width(mc.font, subtitle);
        float textWidth = Math.max(titleWidth, subWidth);

        int fps = mc.getFps();
        int ping = getPing(mc);

        String pingText = config.isShowPing() ? ping + " ms" : "";
        String fpsText = config.isShowFps() ? fps + " fps" : "";

        float pingTextWidth = TextUtils.width(mc.font, pingText);
        float fpsTextWidth = TextUtils.width(mc.font, fpsText);
        float statsTextWidth = Math.max(pingTextWidth, fpsTextWidth);

        float width = logoSize + logoPadding + paddingLeft + textWidth + statsPadding + statsTextWidth + paddingRight;

        // Цвета из конфига
        int bgColor = withAlpha(config.getBackgroundColor(), (int) (alpha * 255));
        int textColor = withAlpha(config.getTitleColor(), (int) (alpha * 255));
        int subColor = withAlpha(config.getSubtitleColor(), (int) (alpha * 255));

        int pingColor = withAlpha(
            ping < 80 ? DisplayUtils.rgb(0, 255, 120) :
            ping < 150 ? DisplayUtils.rgb(255, 200, 0) :
            DisplayUtils.rgb(255, 80, 80),
            (int) (alpha * 255)
        );

        int fpsColor = withAlpha(DisplayUtils.rgb(80, 160, 255), (int) (alpha * 255));

        // Рисуем фон
        DisplayUtils.drawRoundedRect(graphics, x, y, width, height, radius, bgColor);

        // Рендерим логотип
        LogoRenderer logoRenderer = LogoRenderer.getInstance();
        if (logoRenderer != null) {
            logoRenderer.renderLogo(graphics, x + logoPadding, y + (height - logoSize) / 2f, logoSize);
        }

        float textX = x + logoSize + logoPadding + paddingLeft;
        float titleY = y + (height - titleSize) / 2f - 2f * scale;
        float subY = y + (height - subSize) / 2f + 8f * scale;

        // Рендерим основной текст
        TextUtils.draw(graphics, mc.font, title, textX, titleY, textColor, true);
        if (!subtitle.isEmpty()) {
            TextUtils.draw(graphics, mc.font, subtitle, textX, subY, subColor, true);
        }

        // Статистика справа
        float statsRightX = x + width - paddingRight - statsTextWidth;

        if (config.isShowPing()) {
            TextUtils.draw(graphics, mc.font, pingText, statsRightX, titleY, pingColor, true);
        }
        if (config.isShowFps()) {
            TextUtils.draw(graphics, mc.font, fpsText, statsRightX, subY, fpsColor, true);
        }
    }

    /**
     * Получить пинг игрока
     */
    private int getPing(Minecraft mc) {
        if (mc.player == null || mc.getConnection() == null) {
            return 0;
        }
        PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return playerInfo != null ? playerInfo.getLatency() : 0;
    }

    /**
     * Установить альфа-канал цвета
     */
    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Получить текущую прозрачность (0-1)
     */
    public float getAlpha() {
        return fadeAnimation.getValue();
    }

    /**
     * Получить текущий масштаб (0-1)
     */
    public float getScale() {
        return scaleAnimation.getValue();
    }
}
