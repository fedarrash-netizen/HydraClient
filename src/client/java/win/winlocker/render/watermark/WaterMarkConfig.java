package win.winlocker.render.watermark;

/**
 * Конфигурация для WaterMarkRenderer
 */
public class WaterMarkConfig {
    private boolean enabled;
    private float posX;
    private float posY;
    private float scale;
    private int backgroundColor;
    private int titleColor;
    private int subtitleColor;
    private boolean showPing;
    private boolean showFps;
    private boolean showUid;
    private String customTitle;
    private String customSubtitle;
    private int animationDuration;

    public WaterMarkConfig() {
        this.enabled = true;
        this.posX = 15f;
        this.posY = 15f;
        this.scale = 1.0f;
        this.backgroundColor = 0xFF242424;
        this.titleColor = 0xFFFFFFFF;
        this.subtitleColor = 0xFFFFFFFF;
        this.showPing = true;
        this.showFps = true;
        this.showUid = true;
        this.customTitle = "Hydra";
        this.customSubtitle = "Uid: 1";
        this.animationDuration = 300;
    }

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getScale() {
        return scale;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getSubtitleColor() {
        return subtitleColor;
    }

    public boolean isShowPing() {
        return showPing;
    }

    public boolean isShowFps() {
        return showFps;
    }

    public boolean isShowUid() {
        return showUid;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public String getCustomSubtitle() {
        return customSubtitle;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    // Setters
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setSubtitleColor(int subtitleColor) {
        this.subtitleColor = subtitleColor;
    }

    public void setShowPing(boolean showPing) {
        this.showPing = showPing;
    }

    public void setShowFps(boolean showFps) {
        this.showFps = showFps;
    }

    public void setShowUid(boolean showUid) {
        this.showUid = showUid;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public void setCustomSubtitle(String customSubtitle) {
        this.customSubtitle = customSubtitle;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }
}
