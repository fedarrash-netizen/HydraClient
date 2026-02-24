package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;

import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.KeyBindSetting;

public class AntiAnyDesk extends Module {
    
    private final BooleanSetting blockMouse = new BooleanSetting("Блокировать мышь", true);
    private final BooleanSetting blockKeyboard = new BooleanSetting("Блокировать клавиатуру", true);
    private final BooleanSetting autoDisable = new BooleanSetting("Авто-выключение", true);
    private final KeyBindSetting forceDisable = new KeyBindSetting("Принудительно выключить", 0);
    
    private boolean isChatOpen = false;
    private boolean hasTypedAnyDesk = false;
    private long lastChatCheck = 0;
    private String currentChatMessage = "";
    
    public AntiAnyDesk() {
        super("AntiAnyDesk", Module.Category.MISC);
        
        addSetting(blockMouse);
        addSetting(blockKeyboard);
        addSetting(autoDisable);
        addSetting(forceDisable);
    }
    
    @Override
    public void onEnable() {
        isChatOpen = false;
        hasTypedAnyDesk = false;
        currentChatMessage = "";
        lastChatCheck = 0;
    }
    
    @Override
    public void onDisable() {
        // Разблокируем все при выключении
        isChatOpen = false;
        hasTypedAnyDesk = false;
    }
    
    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // Проверяем открыт ли чат
        boolean chatWasOpen = isChatOpen;
        isChatOpen = mc.screen instanceof ChatScreen;
        
        // Если чат только что открылся, сбрасываем сообщение
        if (isChatOpen && !chatWasOpen) {
            currentChatMessage = "";
        }
        
        // Проверяем ввод в чате
        if (isChatOpen) {
            checkChatInput(mc);
        }
        
        // Блокируем мышь и клавиатуру если нужно
        if (!isChatOpen || !hasTypedAnyDesk) {
            blockInput(mc);
        }
        
        // Авто-выключение если написали AnyDesk
        if (autoDisable.get() && hasTypedAnyDesk && isChatOpen) {
            // Ждем немного чтобы сообщение отправилось
            if (System.currentTimeMillis() - lastChatCheck > 1000) {
                setEnabled(false);
            }
        }
        
        // Принудительное выключение по бинду
        if (forceDisable.getKey() != 0 && isKeyPressed(forceDisable.getKey())) {
            setEnabled(false);
        }
    }
    
    private void checkChatInput(Minecraft mc) {
        if (!(mc.screen instanceof ChatScreen)) return;
        
        // Получаем текущее сообщение из чата (упрощенный способ)
        try {
            // Проверяем последние символы в сообщении
            if (currentChatMessage.length() >= 7) {
                String lastChars = currentChatMessage.substring(currentChatMessage.length() - 7).toLowerCase();
                if (lastChars.contains("anydesk")) {
                    hasTypedAnyDesk = true;
                    lastChatCheck = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки доступа к чату
        }
    }
    
    private void blockInput(Minecraft mc) {
        // Блокируем мышь
        if (blockMouse.get()) {
            try {
                MouseHandler mouse = mc.mouseHandler;
                if (mouse != null) {
                    // Блокируем движение мыши - просто ничего не делаем
                    // Блокировка будет работать через mixin
                }
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
        
        // Блокируем клавиатуру
        if (blockKeyboard.get()) {
            try {
                KeyboardHandler keyboard = mc.keyboardHandler;
                if (keyboard != null) {
                    // Блокируем большинство клавиш кроме чата
                    // Это будет работать через mixin
                }
            } catch (Exception e) {
                // Игнорируем ошибки
            }
        }
    }
    
    private boolean isKeyPressed(int key) {
        try {
            return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Метод для обновления сообщения из чата (вызывается из mixin)
    public void updateChatMessage(String message) {
        this.currentChatMessage = message;
    }
    
    // Метод для проверки блокировки клавиши (вызывается из mixin)
    public boolean shouldBlockKey(int key) {
        if (!isEnabled()) return false;
        if (!blockKeyboard.get()) return false;
        if (isChatOpen && hasTypedAnyDesk) return false;
        if (isChatOpen) return false; // Разрешаем ввод в чате
        
        // Разрешаем основные клавиши для управления
        return key != GLFW.GLFW_KEY_ESCAPE && 
               key != GLFW.GLFW_KEY_TAB &&
               key != GLFW.GLFW_KEY_ENTER &&
               key != GLFW.GLFW_KEY_T; // Разрешаем открыть чат
    }
    
    // Метод для проверки блокировки мыши (вызывается из mixin)
    public boolean shouldBlockMouse() {
        if (!isEnabled()) return false;
        if (!blockMouse.get()) return false;
        if (isChatOpen && hasTypedAnyDesk) return false;
        
        return true;
    }
    
    public boolean isChatOpen() {
        return isChatOpen;
    }
    
    public boolean hasTypedAnyDesk() {
        return hasTypedAnyDesk;
    }
}
