package win.winlocker.DropDown;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import win.winlocker.DropDown.settings.ColorSetting;
import win.winlocker.utils.render.DisplayUtils;

import java.awt.Color;

public class ColorPicker extends Screen {
    private final Screen parent;
    private final ColorSetting colorSetting;
    
    private float hue, saturation, brightness;
    private int alpha;
    
    private boolean draggingHue, draggingPicker, draggingAlpha;
    
    private static final int PICKER_SIZE = 120;
    private static final int SLIDER_WIDTH = 20;
    private static final int SLIDER_HEIGHT = 120;
    
    private int pickerX, pickerY;
    private int hueSliderX, hueSliderY;
    private int alphaSliderX, alphaSliderY;
    
    public ColorPicker(Screen parent, ColorSetting colorSetting) {
        super(Component.literal("Color Picker"));
        this.parent = parent;
        this.colorSetting = colorSetting;
        
        // Инициализация значений из текущего цвета
        this.hue = colorSetting.getHue();
        this.saturation = colorSetting.getSaturation();
        this.brightness = colorSetting.getBrightness();
        this.alpha = colorSetting.getAlpha();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Центрирование панелей
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        pickerX = centerX - PICKER_SIZE / 2 - SLIDER_WIDTH / 2 - 10;
        pickerY = centerY - PICKER_SIZE / 2 - 30;
        
        hueSliderX = pickerX + PICKER_SIZE + 10;
        hueSliderY = pickerY;
        
        alphaSliderX = hueSliderX + SLIDER_WIDTH + 10;
        alphaSliderY = pickerY;
    }
    
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Рендер темного фона
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Рендер фона
        renderBackground(graphics, mouseX, mouseY, partialTick);
        
        // Панель
        int panelWidth = 280;
        int panelHeight = 180;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;
        
        // Фон панели
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xC0101010);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFF303030);
        graphics.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFF303030);
        graphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFF303030);
        graphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF303030);
        
        // Заголовок
        graphics.drawString(this.font, "Color Picker: " + colorSetting.getName(), panelX + 10, panelY + 8, 0xFFFFFFFF, false);
        
        // Рендер пикера насыщенности/яркости
        renderPicker(graphics, mouseX, mouseY);
        
        // Рендер слайдера оттенка
        renderHueSlider(graphics, mouseX, mouseY);
        
        // Рендер слайдера альфы
        renderAlphaSlider(graphics, mouseX, mouseY);
        
        // Предпросмотр цвета
        renderColorPreview(graphics, panelX, panelY + panelHeight - 35);
        
        // Информация о цвете
        renderColorInfo(graphics, panelX, panelY + panelHeight - 20);
        
        // Кнопка закрытия
        renderCloseButton(graphics, panelX + panelWidth - 25, panelY + 5, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    private void renderPicker(GuiGraphics g, int mouseX, int mouseY) {
        // Рендер градиента насыщенности/яркости
        for (int y = 0; y < PICKER_SIZE; y++) {
            for (int x = 0; x < PICKER_SIZE; x++) {
                float s = (float) x / PICKER_SIZE;
                float b = 1f - (float) y / PICKER_SIZE;
                int rgb = Color.HSBtoRGB(hue, s, b);
                g.fill(pickerX + x, pickerY + y, pickerX + x + 1, pickerY + y + 1, 0xFF000000 | rgb);
            }
        }
        
        // Рамка
        g.fill(pickerX - 1, pickerY - 1, pickerX + PICKER_SIZE + 1, pickerY, 0xFF303030);
        g.fill(pickerX - 1, pickerY + PICKER_SIZE, pickerX + PICKER_SIZE + 1, pickerY + PICKER_SIZE + 1, 0xFF303030);
        g.fill(pickerX - 1, pickerY, pickerX, pickerY + PICKER_SIZE, 0xFF303030);
        g.fill(pickerX + PICKER_SIZE, pickerY, pickerX + PICKER_SIZE + 1, pickerY + PICKER_SIZE, 0xFF303030);
        
        // Индикатор позиции
        int indicatorX = pickerX + (int) (saturation * PICKER_SIZE);
        int indicatorY = pickerY + (int) ((1 - brightness) * PICKER_SIZE);
        g.fill(indicatorX - 3, indicatorY - 3, indicatorX + 4, indicatorY + 4, 0xFFFFFFFF);
        g.fill(indicatorX - 2, indicatorY - 2, indicatorX + 3, indicatorY + 3, 0xFF000000);
        
        // Обработка перетаскивания
        if (draggingPicker) {
            saturation = Math.max(0, Math.min(1, (mouseX - pickerX) / (float) PICKER_SIZE));
            brightness = Math.max(0, Math.min(1, 1 - (mouseY - pickerY) / (float) PICKER_SIZE));
            updateColor();
        }
    }
    
    private void renderHueSlider(GuiGraphics g, int mouseX, int mouseY) {
        // Рендер градиента оттенка
        for (int i = 0; i < SLIDER_HEIGHT; i++) {
            float h = 1f - (float) i / SLIDER_HEIGHT;
            int rgb = Color.HSBtoRGB(h, 1f, 1f);
            g.fill(hueSliderX, hueSliderY + i, hueSliderX + SLIDER_WIDTH, hueSliderY + i + 1, 0xFF000000 | rgb);
        }
        
        // Рамка
        g.fill(hueSliderX - 1, hueSliderY - 1, hueSliderX + SLIDER_WIDTH + 1, hueSliderY, 0xFF303030);
        g.fill(hueSliderX - 1, hueSliderY + SLIDER_HEIGHT, hueSliderX + SLIDER_WIDTH + 1, hueSliderY + SLIDER_HEIGHT + 1, 0xFF303030);
        g.fill(hueSliderX - 1, hueSliderY, hueSliderX, hueSliderY + SLIDER_HEIGHT, 0xFF303030);
        g.fill(hueSliderX + SLIDER_WIDTH, hueSliderY, hueSliderX + SLIDER_WIDTH + 1, hueSliderY + SLIDER_HEIGHT, 0xFF303030);
        
        // Индикатор
        int indicatorY = hueSliderY + (int) ((1 - hue) * SLIDER_HEIGHT);
        g.fill(hueSliderX - 3, indicatorY - 1, hueSliderX + SLIDER_WIDTH + 3, indicatorY + 2, 0xFFFFFFFF);
        g.fill(hueSliderX - 2, indicatorY, hueSliderX + SLIDER_WIDTH + 2, indicatorY + 1, 0xFF000000);
        
        // Обработка перетаскивания
        if (draggingHue) {
            hue = Math.max(0, Math.min(1, 1 - (mouseY - hueSliderY) / (float) SLIDER_HEIGHT));
            updateColor();
        }
    }
    
    private void renderAlphaSlider(GuiGraphics g, int mouseX, int mouseY) {
        // Базовый цвет (текущий оттенок)
        int baseRgb = Color.HSBtoRGB(hue, saturation, brightness);
        
        // Рендер градиента альфы
        for (int i = 0; i < SLIDER_HEIGHT; i++) {
            float a = 1f - (float) i / SLIDER_HEIGHT;
            int r = (int) ((baseRgb >> 16 & 0xFF) * a);
            int gr = (int) ((baseRgb >> 8 & 0xFF) * a);
            int b = (int) ((baseRgb & 0xFF) * a);
            g.fill(alphaSliderX, alphaSliderY + i, alphaSliderX + SLIDER_WIDTH, alphaSliderY + i + 1, 0xFF000000 | (r << 16) | (gr << 8) | b);
        }
        
        // Рамка
        g.fill(alphaSliderX - 1, alphaSliderY - 1, alphaSliderX + SLIDER_WIDTH + 1, alphaSliderY, 0xFF303030);
        g.fill(alphaSliderX - 1, alphaSliderY + SLIDER_HEIGHT, alphaSliderX + SLIDER_WIDTH + 1, alphaSliderY + SLIDER_HEIGHT + 1, 0xFF303030);
        g.fill(alphaSliderX - 1, alphaSliderY, alphaSliderX, alphaSliderY + SLIDER_HEIGHT, 0xFF303030);
        g.fill(alphaSliderX + SLIDER_WIDTH, alphaSliderY, alphaSliderX + SLIDER_WIDTH + 1, alphaSliderY + SLIDER_HEIGHT, 0xFF303030);
        
        // Индикатор
        int indicatorY = alphaSliderY + (int) ((1 - alpha / 255f) * SLIDER_HEIGHT);
        g.fill(alphaSliderX - 3, indicatorY - 1, alphaSliderX + SLIDER_WIDTH + 3, indicatorY + 2, 0xFFFFFFFF);
        g.fill(alphaSliderX - 2, indicatorY, alphaSliderX + SLIDER_WIDTH + 2, indicatorY + 1, 0xFF000000);
        
        // Обработка перетаскивания
        if (draggingAlpha) {
            alpha = (int) (Math.max(0, Math.min(255, 255 * (1 - (mouseY - alphaSliderY) / (float) SLIDER_HEIGHT))));
            updateColor();
        }
    }
    
    private void renderColorPreview(GuiGraphics g, int x, int y) {
        int currentRgb = colorSetting.get();
        int newRgb = (alpha << 24) | (Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF);
        
        g.drawString(this.font, "Preview:", x + 10, y + 3, 0xFFDDDDDD, false);
        
        // Старый цвет
        g.fill(x + 70, y, x + 90, y + 12, 0xFF000000);
        g.fill(x + 71, y + 1, x + 89, y + 11, currentRgb);
        g.drawString(this.font, "Old", x + 73, y + 3, 0xFF888888, false);
        
        // Новый цвет
        g.fill(x + 95, y, x + 115, y + 12, 0xFF000000);
        g.fill(x + 96, y + 1, x + 114, y + 11, newRgb);
        g.drawString(this.font, "New", x + 98, y + 3, 0xFF888888, false);
    }
    
    private void renderColorInfo(GuiGraphics g, int x, int y) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int newRgb = (alpha << 24) | (rgb & 0xFFFFFF);
        String hex = String.format("#%08X", newRgb);
        int r = (newRgb >> 16) & 0xFF;
        int gr = (newRgb >> 8) & 0xFF;
        int b = newRgb & 0xFF;
        
        g.drawString(this.font, "HEX: " + hex, x + 10, y + 3, 0xFF888888, false);
        g.drawString(this.font, "RGB: " + r + ", " + gr + ", " + b, x + 10, y + 13, 0xFF888888, false);
    }
    
    private void renderCloseButton(GuiGraphics g, int x, int y, int mouseX, int mouseY) {
        boolean hover = mouseX >= x && mouseX <= x + 18 && mouseY >= y && mouseY <= y + 18;
        int color = hover ? 0xFFC04040 : 0xFF803030;
        
        g.fill(x, y, x + 18, y + 18, color);
        g.fill(x + 1, y + 1, x + 17, y + 17, hover ? 0xFFE05050 : 0xFF904040);
        
        // X символ
        g.drawString(this.font, "X", x + 6, y + 4, 0xFFFFFFFF, false);
        
        // Обработка клика
        if (hover && GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            close();
        }
    }
    
    private void updateColor() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int gr = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        colorSetting.setRGBA(r, gr, b, alpha);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Проверка клика по пикеру
            if (mouseX >= pickerX && mouseX <= pickerX + PICKER_SIZE &&
                mouseY >= pickerY && mouseY <= pickerY + PICKER_SIZE) {
                draggingPicker = true;
                return true;
            }
            
            // Проверка клика по слайдеру оттенка
            if (mouseX >= hueSliderX && mouseX <= hueSliderX + SLIDER_WIDTH &&
                mouseY >= hueSliderY && mouseY <= hueSliderY + SLIDER_HEIGHT) {
                draggingHue = true;
                return true;
            }
            
            // Проверка клика по слайдеру альфы
            if (mouseX >= alphaSliderX && mouseX <= alphaSliderX + SLIDER_WIDTH &&
                mouseY >= alphaSliderY && mouseY <= alphaSliderY + SLIDER_HEIGHT) {
                draggingAlpha = true;
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingPicker = false;
            draggingHue = false;
            draggingAlpha = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && (draggingPicker || draggingHue || draggingAlpha)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void close() {
        Minecraft.getInstance().setScreen(parent);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
