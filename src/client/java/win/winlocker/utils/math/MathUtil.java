package win.winlocker.utils.math;

import net.minecraft.client.Minecraft;

public class MathUtil {
    
    public static int calculatePing() {
        try {
            if (Minecraft.getInstance().getConnection() != null) {
                return Minecraft.getInstance().getConnection().getPlayerInfo(Minecraft.getInstance().player.getUUID()).getLatency();
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        return 0;
    }
    
    public static float interpolate(float current, float old, float partialTicks) {
        return old + (current - old) * partialTicks;
    }
    
    public static double interpolate(double current, double old, float partialTicks) {
        return old + (current - old) * partialTicks;
    }
}
