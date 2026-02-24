package win.winlocker.DropDown.settings;

import java.util.function.Supplier;

public class SliderSetting extends Setting {
	private final double min;
	private final double max;
	private double value;
	public Supplier<Boolean> visible = () -> true;

	public SliderSetting(String name, double value, double min, double max) {
		super(name);
		this.min = min;
		this.max = max;
		this.value = clamp(value);
	}

	public SliderSetting(String name, double value, double min, double max, Supplier<Boolean> visible) {
		super(name);
		this.min = min;
		this.max = max;
		this.value = clamp(value);
		this.visible = visible;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double get() {
		return value;
	}

	public int getInt() {
		return (int) Math.round(value);
	}

	public void set(double value) {
		this.value = clamp(value);
	}

	public double getPercent() {
		if (max - min == 0.0) {
			return 0.0;
		}
		return (value - min) / (max - min);
	}

	public void setPercent(double percent) {
		set(min + (max - min) * clamp01(percent));
	}

	public boolean isVisible() {
		return visible.get();
	}

	private double clamp(double v) {
		return Math.max(min, Math.min(max, v));
	}

	private static double clamp01(double v) {
		return Math.max(0.0, Math.min(1.0, v));
	}
}
