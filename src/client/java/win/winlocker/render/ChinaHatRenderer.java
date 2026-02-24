package win.winlocker.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import win.winlocker.module.render.ChinaHat;

import java.util.ArrayList;
import java.util.List;

public class ChinaHatRenderer {
    private static ChinaHatRenderer instance;
    
    public static ChinaHatRenderer getInstance() {
        if (instance == null) {
            instance = new ChinaHatRenderer();
        }
        return instance;
    }
    
    public void renderChinaHats(PoseStack poseStack, MultiBufferSource bufferSource, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        ChinaHat chinaHat = ChinaHat.getInstance();
        if (chinaHat == null || !chinaHat.isEnabled()) return;
        
        // Рендерим шляпу на себе
        if (chinaHat.shouldRenderOnSelf()) {
            renderChinaHat(poseStack, bufferSource, mc.player, tickDelta);
        }
        
        // Рендерим шляпы на других игроках
        if (chinaHat.shouldRenderOnOthers()) {
            for (Player player : mc.level.players()) {
                if (player != mc.player) {
                    renderChinaHat(poseStack, bufferSource, player, tickDelta);
                }
            }
        }
    }
    
    private void renderChinaHat(PoseStack poseStack, MultiBufferSource bufferSource, Player player, float tickDelta) {
        ChinaHat chinaHat = ChinaHat.getInstance();
        if (chinaHat == null) return;
        
        // Получаем позицию игрока
        Vec3 playerPos = player.getPosition(tickDelta);
        double x = playerPos.x;
        double y = playerPos.y + player.getEyeHeight() + chinaHat.getHatYOffset();
        double z = playerPos.z;
        
        // Сохраняем текущую матрицу
        poseStack.pushPose();
        
        // Перемещаемся к позиции шляпы
        poseStack.translate(x, y, z);
        
        // Получаем цвет
        int color = chinaHat.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        
        // Рендерим китайскую шляпу (конус)
        renderCone(poseStack, bufferSource, chinaHat.getHatRadius(), chinaHat.getHatHeight(), r, g, b, a);
        
        // Восстанавливаем матрицу
        poseStack.popPose();
    }
    
    private void renderCone(PoseStack poseStack, MultiBufferSource bufferSource, double radius, double height, float r, float g, float b, float a) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        
        int segments = 16; // Количество сегментов для круга
        
        // Рендерим линии конуса
        for (int i = 0; i < segments; i++) {
            double angle1 = (2 * Math.PI * i) / segments;
            double angle2 = (2 * Math.PI * (i + 1)) / segments;
            
            // Нижняя точка конуса
            double x1 = radius * Math.cos(angle1);
            double z1 = radius * Math.sin(angle1);
            double x2 = radius * Math.cos(angle2);
            double z2 = radius * Math.sin(angle2);
            
            // Линии от основания к вершине
            consumer.addVertex(poseStack.last().pose(), (float)x1, (float)height, (float)z1).setColor(r, g, b, a);
            consumer.addVertex(poseStack.last().pose(), 0, 0, 0).setColor(r, g, b, a);
            
            // Линии основания
            consumer.addVertex(poseStack.last().pose(), (float)x1, (float)height, (float)z1).setColor(r, g, b, a);
            consumer.addVertex(poseStack.last().pose(), (float)x2, (float)height, (float)z2).setColor(r, g, b, a);
        }
        
        // Рендерим заливку (если нужно)
        if (a > 0.1f) {
            renderConeFill(poseStack, bufferSource, radius, height, r, g, b, a * 0.3f);
        }
    }
    
    private void renderConeFill(PoseStack poseStack, MultiBufferSource bufferSource, double radius, double height, float r, float g, float b, float a) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        
        int segments = 16;
        
        // Рендерим треугольники для заливки конуса
        for (int i = 0; i < segments; i++) {
            double angle1 = (2 * Math.PI * i) / segments;
            double angle2 = (2 * Math.PI * (i + 1)) / segments;
            
            double x1 = radius * Math.cos(angle1);
            double z1 = radius * Math.sin(angle1);
            double x2 = radius * Math.cos(angle2);
            double z2 = radius * Math.sin(angle2);
            
            // Треугольник: вершина - точка1 - точка2
            consumer.addVertex(poseStack.last().pose(), 0, 0, 0).setColor(r, g, b, a);
            consumer.addVertex(poseStack.last().pose(), (float)x1, (float)height, (float)z1).setColor(r, g, b, a);
            consumer.addVertex(poseStack.last().pose(), (float)x2, (float)height, (float)z2).setColor(r, g, b, a);
        }
    }
}
