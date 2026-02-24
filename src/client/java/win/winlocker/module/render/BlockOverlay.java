package win.winlocker.module.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import win.winlocker.DropDown.settings.BooleanSetting;
import win.winlocker.DropDown.settings.ModeSetting;
import win.winlocker.module.Module;
import win.winlocker.utils.render.DisplayUtils;
import win.winlocker.utils.render.TextUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import java.util.List;

public class BlockOverlay extends Module {
    public final BooleanSetting showInfo = new BooleanSetting("Show Info", true);
    public final BooleanSetting showOutline = new BooleanSetting("Show Outline", true);
    public final ModeSetting effectMode = new ModeSetting("Effect", "None", List.of("None", "Blur", "Stars"));
    public final BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    
    public BlockOverlay() {
        super("BlockOverlay", Category.RENDER);
        addSetting(showInfo);
        addSetting(showOutline);
        addSetting(effectMode);
        addSetting(rainbow);
    }
    
    public void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.font == null) {
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        String blockName = state.getBlock().getName().getString();
        
        // Получаем позицию блока
        Vec3 blockPos = Vec3.atCenterOf(pos);
        Vec3 playerPos = mc.player.getEyePosition();
        double distance = playerPos.distanceTo(blockPos);
        
        // Рендер информации о блоке
        if (showInfo.get()) {
            renderBlockInfo(g, blockName, distance, pos);
        }
    }
    
    public void renderWorld(PoseStack poseStack, MultiBufferSource bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        
        // Рендер обводки блока
        if (showOutline.get()) {
            renderBlockOutline(poseStack, bufferSource, pos);
        }
    }
    
    private void renderBlockOutline(PoseStack poseStack, MultiBufferSource bufferSource, BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        
        BlockState state = level.getBlockState(pos);
        VoxelShape shape = state.getShape(level, pos);
        AABB bb = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
        
        // Получаем цвет
        int color = getColor();
        
        // Рендерим линии обводки
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LINES);
        
        float minX = (float) bb.minX;
        float minY = (float) bb.minY;
        float minZ = (float) bb.minZ;
        float maxX = (float) bb.maxX;
        float maxY = (float) bb.maxY;
        float maxZ = (float) bb.maxZ;
        
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        
        // Рисуем линии куба
        poseStack.pushPose();
        
        // Нижняя грань
        consumer.addVertex(poseStack.last().pose(), minX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, minY, minZ).setColor(red, green, blue, alpha);
        
        // Верхняя грань
        consumer.addVertex(poseStack.last().pose(), minX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, maxY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, maxY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, maxY, minZ).setColor(red, green, blue, alpha);
        
        // Вертикальные линии
        consumer.addVertex(poseStack.last().pose(), minX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, maxY, minZ).setColor(red, green, blue, alpha);
        
        consumer.addVertex(poseStack.last().pose(), maxX, minY, minZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, maxY, minZ).setColor(red, green, blue, alpha);
        
        consumer.addVertex(poseStack.last().pose(), maxX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        
        consumer.addVertex(poseStack.last().pose(), minX, minY, maxZ).setColor(red, green, blue, alpha);
        consumer.addVertex(poseStack.last().pose(), minX, maxY, maxZ).setColor(red, green, blue, alpha);
        
        poseStack.popPose();
    }
    
    private void renderBlockInfo(GuiGraphics g, String blockName, double distance, BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        
        // Размеры оверлея
        int width = 120;
        int height = 25;
        int x = mc.getWindow().getGuiScaledWidth() / 2 - width / 2;
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 40;
        
        // Цвета
        int bgColor = applyEffect(0xC0101010);
        int borderColor = applyEffect(0x603434D4);
        int textColor = applyEffect(0xFFFFFFFF);
        int distanceColor = applyEffect(0xFFAAAAAA);
        
        // Рисуем фон с рамкой
        DisplayUtils.drawRoundedRect(g, x, y, width, height, 6, bgColor);
        DisplayUtils.drawRoundedOutline(g, x, y, width, height, 6, 1, borderColor);
        
        Font font = mc.font;
        
        // Название блока
        String displayName = TextUtils.ellipsize(font, blockName, 74);
        TextUtils.draw(g, font, displayName, x + 8, y + 8, textColor, true);
        
        // Дистанция
        String distanceText = String.format("%.1fm", distance);
        int distanceWidth = TextUtils.width(font, distanceText);
        TextUtils.draw(g, font, distanceText, x + width - distanceWidth - 8, y + 8, distanceColor, true);
        
        // Координаты блока (маленьким текстом)
        String coordsText = String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ());
        TextUtils.draw(g, font, coordsText, x + 8, y + height - 10, applyEffect(0xFF787878), false);
    }
    
    private int getColor() {
        if (rainbow.get()) {
            return getRainbowColor();
        }
        return 0xFF3434D4; // Фиолетовый по умолчанию
    }
    
    private int applyEffect(int baseColor) {
        if (rainbow.get()) {
            return getRainbowColor();
        }
        
        String effect = effectMode.get();
        if (effect.equals("Blur")) {
            // Размытие - делаем цвет более прозрачным
            return (baseColor & 0x00FFFFFF) | 0x80000000; // 50% прозрачность
        } else if (effect.equals("Stars")) {
            // Звездный эффект - добавляем мерцание
            float pulse = (float) (Math.sin(System.currentTimeMillis() * 0.005) * 0.5 + 0.5);
            int alpha = (int) (255 * pulse);
            return (baseColor & 0x00FFFFFF) | (alpha << 24);
        }
        
        return baseColor;
    }
    
    private int getRainbowColor() {
        float hue = (System.currentTimeMillis() % 2000) / 2000.0f;
        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
        return 0xFF000000 | rgb;
    }
}
