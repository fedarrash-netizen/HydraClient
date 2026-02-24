package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import win.winlocker.DropDown.screens.ProxyScreen;

@Mixin(Screen.class)
public class MultiplayerScreenMixin {
    
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        
        // Проверяем что это MultiplayerScreen по названию
        if (screen.getClass().getSimpleName().contains("Multiplayer")) {
            // Добавляем кнопку Proxy в правый верхний угол
            Button proxyButton = Button.builder(Component.literal("Proxy"), button -> {
                Minecraft.getInstance().setScreen(new ProxyScreen(screen));
            })
            .bounds(screen.width - 60, 5, 50, 20)
            .build();
            
            // Используем reflection для доступа к protected методу
            try {
                java.lang.reflect.Method method = Screen.class.getDeclaredMethod("addRenderableWidget", net.minecraft.client.gui.components.events.GuiEventListener.class);
                method.setAccessible(true);
                method.invoke(screen, proxyButton);
            } catch (Exception e) {
                // Если не удалось, просто игнорируем
            }
        }
    }
}
