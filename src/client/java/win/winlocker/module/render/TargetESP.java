package win.winlocker.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LevelRenderer;
import win.winlocker.module.Module;
import win.winlocker.module.ModuleManager;

public class TargetESP extends Module {
    public TargetESP() {
        super("TargetESP", Category.RENDER);
    }

    public static void render(PoseStack matrixStack, float partialTicks) {
        TargetESP mod = (TargetESP) ModuleManager.getModule(TargetESP.class);
        if (mod == null || !mod.isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        for (Entity e : mc.level.entitiesForRendering()) {
            if (e instanceof Player && e != mc.player && e.isAlive()) {
                renderESP(matrixStack, e, partialTicks);
            }
        }
    }

    private static void renderESP(PoseStack matrixStack, Entity entity, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        double x = entity.xOld + (entity.getX() - entity.xOld) * partialTicks - mc.getEntityRenderDispatcher().camera.getPosition().x;
        double y = entity.yOld + (entity.getY() - entity.yOld) * partialTicks - mc.getEntityRenderDispatcher().camera.getPosition().y;
        double z = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks - mc.getEntityRenderDispatcher().camera.getPosition().z;

        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ()).move(x, y, z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        VertexConsumer buffer = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());
        net.minecraft.client.renderer.ShapeRenderer.renderLineBox(matrixStack, buffer, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 1.0f, 0.5f, 1.0f, 1.0f);

        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
