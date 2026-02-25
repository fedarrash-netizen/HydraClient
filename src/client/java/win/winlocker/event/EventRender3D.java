package win.winlocker.event;

import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Событие отрисовки мира (для ESP и других 3D эффектов)
 */
public class EventRender3D extends Event {
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final float partialTicks;

    public EventRender3D(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.partialTicks = partialTicks;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
