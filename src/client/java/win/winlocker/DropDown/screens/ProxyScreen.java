package win.winlocker.DropDown.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;

public class ProxyScreen extends Screen {
    
    private final Screen parent;
    private EditBox hostField;
    private EditBox portField;
    private EditBox usernameField;
    private EditBox passwordField;
    private Button toggleButton;
    private Button connectButton;
    private Button backButton;
    
    // Настройки прокси
    private static final BooleanSetting proxyEnabled = new BooleanSetting("Proxy Enabled", false);
    private static final ModeSetting proxyType = new ModeSetting("Proxy Type", "HTTP", 
            java.util.List.of("HTTP", "SOCKS4", "SOCKS5"));
    
    private boolean proxyConnected = false;
    
    public ProxyScreen(Screen parent) {
        super(Component.literal("Proxy Settings"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        // Поля ввода
        hostField = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, Component.literal("Host"));
        hostField.setValue("127.0.0.1");
        this.addRenderableWidget(hostField);
        
        portField = new EditBox(this.font, this.width / 2 - 100, 90, 200, 20, Component.literal("Port"));
        portField.setValue("8080");
        portField.setFilter(s -> s.matches("\\d*"));
        this.addRenderableWidget(portField);
        
        usernameField = new EditBox(this.font, this.width / 2 - 100, 120, 200, 20, Component.literal("Username"));
        this.addRenderableWidget(usernameField);
        
        passwordField = new EditBox(this.font, this.width / 2 - 100, 150, 200, 20, Component.literal("Password"));
        passwordField.setFilter(s -> true); // Разрешаем все символы для пароля
        this.addRenderableWidget(passwordField);
        
        // Кнопки
        toggleButton = Button.builder(
            Component.literal(proxyEnabled.get() ? "Disable" : "Enable"),
            button -> {
                proxyEnabled.set(!proxyEnabled.get());
                button.setMessage(Component.literal(proxyEnabled.get() ? "Disable" : "Enable"));
            }
        ).bounds(this.width / 2 - 50, 190, 100, 20).build();
        this.addRenderableWidget(toggleButton);
        
        connectButton = Button.builder(
            Component.literal("Connect"),
            button -> connectProxy()
        ).bounds(this.width / 2 - 50, 220, 100, 20).build();
        this.addRenderableWidget(connectButton);
        
        backButton = Button.builder(
            Component.literal("Back"),
            button -> this.minecraft.setScreen(parent)
        ).bounds(this.width / 2 - 50, 250, 100, 20).build();
        this.addRenderableWidget(backButton);
    }
    
    private void connectProxy() {
        String host = hostField.getValue().trim();
        String port = portField.getValue().trim();
        String username = usernameField.getValue().trim();
        String password = passwordField.getValue().trim();
        
        if (host.isEmpty() || port.isEmpty()) {
            // Показать сообщение об ошибке
            return;
        }
        
        try {
            int portNum = Integer.parseInt(port);
            
            // Здесь будет логика подключения к прокси
            // Для примера просто меняем статус
            proxyConnected = true;
            
            // Показать сообщение об успешном подключении
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Proxy connected: " + host + ":" + port), false);
            }
            
        } catch (NumberFormatException e) {
            // Показать сообщение об ошибке порта
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Invalid port number"), false);
            }
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        
        // Заголовок
        graphics.drawCenteredString(this.font, Component.literal("Proxy Settings"), this.width / 2, 20, 0xFFFFFF);
        
        // Подписи полей
        graphics.drawString(this.font, Component.literal("Host:"), this.width / 2 - 150, 65, 0xFFFFFF);
        graphics.drawString(this.font, Component.literal("Port:"), this.width / 2 - 150, 95, 0xFFFFFF);
        graphics.drawString(this.font, Component.literal("Username:"), this.width / 2 - 150, 125, 0xFFFFFF);
        graphics.drawString(this.font, Component.literal("Password:"), this.width / 2 - 150, 155, 0xFFFFFF);
        
        // Статус подключения
        String status = proxyConnected ? "Connected" : "Disconnected";
        int color = proxyConnected ? 0x55FF55 : 0xFF5555;
        graphics.drawString(this.font, Component.literal("Status: " + status), this.width / 2 - 150, 280, color);
        
        // Тип прокси
        graphics.drawString(this.font, Component.literal("Type: " + proxyType.get()), this.width / 2 - 150, 300, 0xFFFFFF);
        
        super.render(graphics, mouseX, mouseY, delta);
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
    
    // Геттеры для настроек
    public static boolean isProxyEnabled() {
        return proxyEnabled.get();
    }
    
    public static String getProxyType() {
        return proxyType.get();
    }
    
    public static String getProxyHost() {
        // В реальном коде здесь будет хранение настроек
        return "127.0.0.1";
    }
    
    public static int getProxyPort() {
        // В реальном коде здесь будет хранение настроек
        return 8080;
    }
}
