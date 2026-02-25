package win.winlocker.DropDown.settings;

import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting {
	private final List<String> modes;
	private int index;
	public Supplier<Boolean> visible = () -> true;

	public ModeSetting(String name, String defaultMode, List<String> modes) {
		super(name);
		this.modes = List.copyOf(modes);
		this.index = Math.max(0, this.modes.indexOf(defaultMode));
	}

	public ModeSetting(String name, String defaultMode, List<String> modes, Supplier<Boolean> visible) {
		super(name);
		this.modes = List.copyOf(modes);
		this.index = Math.max(0, this.modes.indexOf(defaultMode));
		this.visible = visible;
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

	public boolean is(String mode) {
		return get().equalsIgnoreCase(mode);
	}

	public boolean isVisible() {
		return visible.get();
	}

	public void set(String mode) {
		int newIndex = modes.indexOf(mode);
		if (newIndex >= 0) {
			this.index = newIndex;
		}
	}
}
