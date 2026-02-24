package win.winlocker;

import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.KeyBindSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.DropDown.settings.ModuleSetting;
import win.winlocker.DropDown.settings.SliderSetting;

import java.util.List;

public final class ClientSettings {
	private ClientSettings() {
	}

	public enum Tab {
		MISC("Misc"),
		RENDER("Render"),
		COMBAT("Combat"),
		MOVEMENT("Movement");

		private final String title;

		Tab(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	public static Tab clickGuiTab = Tab.RENDER;

	public static int keyBindsHudX = 6;
	public static int keyBindsHudY = 28;

	public static final ModuleSetting PARTICLES = new ModuleSetting("Particles");
	public static final BooleanSetting PARTICLES_ENABLED = PARTICLES.add(new BooleanSetting("Enabled", true));
	public static final SliderSetting PARTICLES_COUNT = PARTICLES.add(new SliderSetting("Count", 60.0, 0.0, 200.0));
	public static final ModeSetting PARTICLES_STAR_MODE = PARTICLES.add(new ModeSetting("Star", "Default", List.of("Default")));
	public static final KeyBindSetting PARTICLES_KEY = PARTICLES.add(new KeyBindSetting("Bind", 0));

	public static final ModuleSetting TARGETING = new ModuleSetting("Targeting");
	public static final BooleanSetting TARGETING_ENABLED = TARGETING.add(new BooleanSetting("Enabled", false));
	public static final SliderSetting TARGETING_RANGE = TARGETING.add(new SliderSetting("Range", 4.0, 1.0, 6.0));
	public static final ModeSetting TARGETING_MODE = TARGETING.add(new ModeSetting("Mode", "Visual", List.of("Visual")));
	public static final KeyBindSetting TARGETING_KEY = TARGETING.add(new KeyBindSetting("Bind", 0));

	public static final ModuleSetting BOTAIM = new ModuleSetting("BotAim");
	public static final BooleanSetting BOTAIM_ENABLED = BOTAIM.add(new BooleanSetting("Enabled", false));
	public static final SliderSetting BOTAIM_FOV = BOTAIM.add(new SliderSetting("Fov", 60.0, 10.0, 180.0));
	public static final ModeSetting BOTAIM_MODE = BOTAIM.add(new ModeSetting("Mode", "Visual", List.of("Visual")));
	public static final KeyBindSetting BOTAIM_KEY = BOTAIM.add(new KeyBindSetting("Bind", 0));
}
