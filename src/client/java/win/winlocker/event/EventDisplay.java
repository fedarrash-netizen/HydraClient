package win.winlocker.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Событие отрисовки HUD
 */
public class EventDisplay extends Event {
    private final GuiGraphics guiGraphics;
    private final PoseStack matrixStack;
    private final float partialTicks;

    public EventDisplay(GuiGraphics guiGraphics, float partialTicks) {
        this.guiGraphics = guiGraphics;
        this.matrixStack = guiGraphics.pose();
        this.partialTicks = partialTicks;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public PoseStack getMatrixStack() {
        return matrixStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
