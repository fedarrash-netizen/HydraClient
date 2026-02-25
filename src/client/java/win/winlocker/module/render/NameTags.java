package win.winlocker.module.render;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.DropDown.settings.SliderSetting;

/**
 * Модуль для NameTags
 */
public class NameTags extends Module {
    private final BooleanSetting showHealth;
    private final BooleanSetting showArmor;
    private final BooleanSetting showItem;
    private final BooleanSetting showDistance;
    private final BooleanSetting showFriends;
    private final BooleanSetting centered;
    private final ColorSetting backgroundColor;
    private final ColorSetting nameColor;
    private final SliderSetting range;
    private final SliderSetting scale;

    public NameTags() {
        super("NameTags", Category.RENDER);

        this.showHealth = new BooleanSetting("Show Health", true);
        this.showArmor = new BooleanSetting("Show Armor", true);
        this.showItem = new BooleanSetting("Show Item", false);
        this.showDistance = new BooleanSetting("Show Distance", false);
        this.showFriends = new BooleanSetting("Show Friends", true);
        this.centered = new BooleanSetting("Centered", true);
        this.backgroundColor = new ColorSetting("Background", 0x90000000);
        this.nameColor = new ColorSetting("Name Color", 0xFFFFFFFF);
        this.range = new SliderSetting("Range", 100f, 10f, 500f);
        this.scale = new SliderSetting("Scale", 1f, 0.5f, 2f);

        addSetting(showHealth);
        addSetting(showArmor);
        addSetting(showItem);
        addSetting(showDistance);
        addSetting(showFriends);
        addSetting(centered);
        addSetting(backgroundColor);
        addSetting(nameColor);
        addSetting(range);
        addSetting(scale);
    }

    public boolean shouldShowHealth() {
        return showHealth.get();
    }

    public boolean shouldShowArmor() {
        return showArmor.get();
    }

    public boolean shouldShowItem() {
        return showItem.get();
    }

    public boolean shouldShowDistance() {
        return showDistance.get();
    }

    public boolean shouldShowFriends() {
        return showFriends.get();
    }

    public boolean isCentered() {
        return centered.get();
    }

    public int getBackgroundColor() {
        return backgroundColor.get();
    }

    public int getNameColor() {
        return nameColor.get();
    }

    public float getRange() {
        return (float) range.get();
    }

    public float getScale() {
        return (float) scale.get();
    }
}
