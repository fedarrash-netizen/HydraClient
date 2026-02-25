package win.winlocker.render.nametags;

/**
 * Конфигурация для NameTagsRenderer
 */
public class NameTagsConfig {
    private boolean enabled;
    private float maxRange;
    private boolean sortByDistance;
    
    // Внешний вид
    private int backgroundColor;
    private int borderColor;
    private float borderWidth;
    private float cornerRadius;
    private boolean roundedCorners;
    private boolean shadowEnabled;
    private float shadowRadius;
    
    // Отступы
    private float paddingX;
    private float paddingY;
    
    // Текст
    private int nameColor;
    private int infoColor;
    private boolean textShadow;
    private boolean centered;
    
    // Префиксы
    private boolean showPrefix;
    private String prefix;
    private boolean hideOwnName;
    private String ownNameReplacement;
    
    // Информация
    private boolean showInfo;
    private boolean showHealth;
    private boolean showArmor;
    private boolean showItem;
    private boolean showDistance;
    private int distanceColor;
    
    // Друзья
    private boolean showFriendIcon;
    private int friendColor;
    
    // Сущности
    private boolean colorByTeam;

    public NameTagsConfig() {
        this.enabled = true;
        this.maxRange = 100f;
        this.sortByDistance = true;
        
        // Внешний вид
        this.backgroundColor = 0x90000000;
        this.borderColor = 0xFFFFFFFF;
        this.borderWidth = 0f;
        this.cornerRadius = 3f;
        this.roundedCorners = true;
        this.shadowEnabled = true;
        this.shadowRadius = 5f;
        
        // Отступы
        this.paddingX = 4f;
        this.paddingY = 2f;
        
        // Текст
        this.nameColor = 0xFFFFFFFF;
        this.infoColor = 0xFFAAAAAA;
        this.textShadow = true;
        this.centered = true;
        
        // Префиксы
        this.showPrefix = false;
        this.prefix = "";
        this.hideOwnName = false;
        this.ownNameReplacement = "You";
        
        // Информация
        this.showInfo = true;
        this.showHealth = true;
        this.showArmor = true;
        this.showItem = false;
        this.showDistance = false;
        this.distanceColor = 0xFF888888;
        
        // Друзья
        this.showFriendIcon = false;
        this.friendColor = 0xFFFF69B4;
        
        // Сущности
        this.colorByTeam = true;
    }

    // Getters
    public boolean isEnabled() { return enabled; }
    public float getMaxRange() { return maxRange; }
    public boolean isSortByDistance() { return sortByDistance; }
    public int getBackgroundColor() { return backgroundColor; }
    public int getBorderColor() { return borderColor; }
    public float getBorderWidth() { return borderWidth; }
    public float getCornerRadius() { return cornerRadius; }
    public boolean isRoundedCorners() { return roundedCorners; }
    public boolean isShadowEnabled() { return shadowEnabled; }
    public float getShadowRadius() { return shadowRadius; }
    public float getPaddingX() { return paddingX; }
    public float getPaddingY() { return paddingY; }
    public int getNameColor() { return nameColor; }
    public int getInfoColor() { return infoColor; }
    public boolean isTextShadow() { return textShadow; }
    public boolean isCentered() { return centered; }
    public boolean isShowPrefix() { return showPrefix; }
    public String getPrefix() { return prefix; }
    public boolean isHideOwnName() { return hideOwnName; }
    public String getOwnNameReplacement() { return ownNameReplacement; }
    public boolean isShowInfo() { return showInfo; }
    public boolean isShowHealth() { return showHealth; }
    public boolean isShowArmor() { return showArmor; }
    public boolean isShowItem() { return showItem; }
    public boolean isShowDistance() { return showDistance; }
    public int getDistanceColor() { return distanceColor; }
    public boolean isShowFriendIcon() { return showFriendIcon; }
    public int getFriendColor() { return friendColor; }
    public boolean isColorByTeam() { return colorByTeam; }

    // Setters
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setMaxRange(float maxRange) { this.maxRange = maxRange; }
    public void setSortByDistance(boolean sortByDistance) { this.sortByDistance = sortByDistance; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setBorderColor(int borderColor) { this.borderColor = borderColor; }
    public void setBorderWidth(float borderWidth) { this.borderWidth = borderWidth; }
    public void setCornerRadius(float cornerRadius) { this.cornerRadius = cornerRadius; }
    public void setRoundedCorners(boolean roundedCorners) { this.roundedCorners = roundedCorners; }
    public void setShadowEnabled(boolean shadowEnabled) { this.shadowEnabled = shadowEnabled; }
    public void setShadowRadius(float shadowRadius) { this.shadowRadius = shadowRadius; }
    public void setPaddingX(float paddingX) { this.paddingX = paddingX; }
    public void setPaddingY(float paddingY) { this.paddingY = paddingY; }
    public void setNameColor(int nameColor) { this.nameColor = nameColor; }
    public void setInfoColor(int infoColor) { this.infoColor = infoColor; }
    public void setTextShadow(boolean textShadow) { this.textShadow = textShadow; }
    public void setCentered(boolean centered) { this.centered = centered; }
    public void setShowPrefix(boolean showPrefix) { this.showPrefix = showPrefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public void setHideOwnName(boolean hideOwnName) { this.hideOwnName = hideOwnName; }
    public void setOwnNameReplacement(String ownNameReplacement) { this.ownNameReplacement = ownNameReplacement; }
    public void setShowInfo(boolean showInfo) { this.showInfo = showInfo; }
    public void setShowHealth(boolean showHealth) { this.showHealth = showHealth; }
    public void setShowArmor(boolean showArmor) { this.showArmor = showArmor; }
    public void setShowItem(boolean showItem) { this.showItem = showItem; }
    public void setShowDistance(boolean showDistance) { this.showDistance = showDistance; }
    public void setDistanceColor(int distanceColor) { this.distanceColor = distanceColor; }
    public void setShowFriendIcon(boolean showFriendIcon) { this.showFriendIcon = showFriendIcon; }
    public void setFriendColor(int friendColor) { this.friendColor = friendColor; }
    public void setColorByTeam(boolean colorByTeam) { this.colorByTeam = colorByTeam; }
}
