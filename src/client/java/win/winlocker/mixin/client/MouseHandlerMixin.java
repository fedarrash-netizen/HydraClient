package win.winlocker.mixin.client;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import win.winlocker.module.misc.AntiAnyDesk;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void onTurnPlayer(CallbackInfo ci) {
        AntiAnyDesk antiAnyDesk = (AntiAnyDesk) getModule("AntiAnyDesk");
        
        if (antiAnyDesk != null && antiAnyDesk.isEnabled()) {
            if (antiAnyDesk.shouldBlockMouse()) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "onMove", at = @At("HEAD"), cancellable = true)
    private void onMouseMove(long window, double xpos, double ypos, CallbackInfo ci) {
        AntiAnyDesk antiAnyDesk = (AntiAnyDesk) getModule("AntiAnyDesk");
        
        if (antiAnyDesk != null && antiAnyDesk.isEnabled()) {
            if (antiAnyDesk.shouldBlockMouse()) {
                ci.cancel();
            }
        }
    }
    
    private Object getModule(String name) {
        try {
            // Поиск модуля по имени через ModuleManager
            return null; // Заглушка - нужно реализовать
        } catch (Exception e) {
            return null;
        }
    }
}
