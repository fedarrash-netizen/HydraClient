package win.winlocker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import win.winlocker.module.ModuleManager;
import win.winlocker.module.misc.RemoveVisual;
import win.winlocker.module.misc.ModDetect;
import win.winlocker.module.misc.Finder;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == 257 || keyCode == 335) { // Enter
            ChatScreen screen = (ChatScreen) (Object) this;
            String message = ((ChatScreenAccessor) screen).getInput().getValue();
            
            // Проверяем на команду RemoveVisual
            if (message.equalsIgnoreCase("PulsVisual")) {
                RemoveVisual rv = (RemoveVisual) ModuleManager.getModule(RemoveVisual.class);
                if (rv != null && rv.isEnabled()) {
                    rv.setEnabled(false);
                    Minecraft.getInstance().setScreen(null);
                    cir.setReturnValue(true);
                }
            }
            
            // Проверяем на команду .finder
            if (message.startsWith(".finder")) {
                Finder finder = (Finder) ModuleManager.getModule(Finder.class);
                if (finder != null) {
                    if (finder.handleCommand(message)) {
                        Minecraft.getInstance().setScreen(null);
                        cir.setReturnValue(true);
                    }
                }
            }
            
            // Передаем сообщение в ModDetect для проверки
            ModDetect modDetect = (ModDetect) ModuleManager.getModule(ModDetect.class);
            if (modDetect != null) {
                // Получаем имя игрока (в данном случае это текущий игрок)
                String playerName = Minecraft.getInstance().player.getName().getString();
                modDetect.onChatMessageReceived(message, playerName);
            }
        }
    }
}
