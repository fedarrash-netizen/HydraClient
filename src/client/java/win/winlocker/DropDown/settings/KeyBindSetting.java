package win.winlocker.DropDown.settings;

import org.lwjgl.glfw.GLFW;
import java.util.function.Supplier;

public class KeyBindSetting extends Setting {
	private int key;
	public Supplier<Boolean> visible = () -> true;

	public KeyBindSetting(String name, int key) {
		super(name);
		this.key = key;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getKeyName() {
		if (key <= 0) {
			return "NONE";
		}
		String n = GLFW.glfwGetKeyName(key, 0);
		if (n != null && !n.isBlank()) {
			return n.toUpperCase();
		}
		switch (key) {
			case GLFW.GLFW_KEY_LEFT_SHIFT:
				return "LSHIFT";
			case GLFW.GLFW_KEY_RIGHT_SHIFT:
				return "RSHIFT";
			case GLFW.GLFW_KEY_LEFT_CONTROL:
				return "LCTRL";
			case GLFW.GLFW_KEY_RIGHT_CONTROL:
				return "RCTRL";
			case GLFW.GLFW_KEY_LEFT_ALT:
				return "LALT";
			case GLFW.GLFW_KEY_RIGHT_ALT:
				return "RALT";
			case GLFW.GLFW_KEY_SPACE:
				return "SPACE";
			case GLFW.GLFW_KEY_TAB:
				return "TAB";
			case GLFW.GLFW_KEY_ENTER:
				return "ENTER";
			case GLFW.GLFW_KEY_BACKSPACE:
				return "BACKSPACE";
			case GLFW.GLFW_KEY_ESCAPE:
				return "ESC";
			default:
				return "KEY_" + key;
		}
	}

	public boolean isVisible() {
		return visible.get();
	}
}
