package win.winlocker.DropDown.settings;

import java.util.List;

public class ModeSetting extends Setting {
	private final List<String> modes;
	private int index;

	public ModeSetting(String name, String defaultMode, List<String> modes) {
		super(name);
		this.modes = List.copyOf(modes);
		this.index = Math.max(0, this.modes.indexOf(defaultMode));
	}

	public List<String> getModes() {
		return modes;
	}

	public String get() {
		return modes.get(index);
	}

	public void next() {
		index = (index + 1) % modes.size();
	}

	public void prev() {
		index = (index - 1 + modes.size()) % modes.size();
	}
}
