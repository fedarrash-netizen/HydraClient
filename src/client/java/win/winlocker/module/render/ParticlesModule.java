package win.winlocker.module.render;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.SliderSetting;
import win.winlocker.module.Module;

public class ParticlesModule extends Module {
    public static final BooleanSetting enabledSetting = new BooleanSetting("Stars", true);
    public static final SliderSetting countSetting = new SliderSetting("Count", 60.0, 0.0, 200.0);

    public ParticlesModule() {
        super("Particles", Category.RENDER);
        addSetting(enabledSetting);
        addSetting(countSetting);
    }
}
