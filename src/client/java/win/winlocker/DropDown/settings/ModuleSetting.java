package win.winlocker.DropDown.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleSetting {
	private final String name;
	private final List<Setting> settings = new ArrayList<>();

	public ModuleSetting(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Setting> getSettings() {
		return Collections.unmodifiableList(settings);
	}

	public <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}
}
