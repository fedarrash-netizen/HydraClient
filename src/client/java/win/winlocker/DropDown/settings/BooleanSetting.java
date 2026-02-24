package win.winlocker.DropDown.settings;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {
	private boolean value;
	public Supplier<Boolean> visible = () -> true;

	public BooleanSetting(String name, boolean value) {
		super(name);
		this.value = value;
	}

	public BooleanSetting(String name, boolean value, Supplier<Boolean> visible) {
		super(name);
		this.value = value;
		this.visible = visible;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	public void toggle() {
		this.value = !this.value;
	}

	public boolean isVisible() {
		return visible.get();
	}
}
