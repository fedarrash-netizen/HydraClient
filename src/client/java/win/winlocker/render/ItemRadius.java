package win.winlocker.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import win.winlocker.module.Module;
import win.winlocker.DropDown.settings.BooleanSetting;

import java.util.Random;

public class ItemRadius extends Module {

    private final BooleanSetting dezka = new BooleanSetting("Дезка", true);
    private final BooleanSetting yavka = new BooleanSetting("Явка", true);
    private final BooleanSetting fireCharge = new BooleanSetting("Огненый Заряд", true);
    private final BooleanSetting godsAura = new BooleanSetting("Божья Аура", true);
    private final BooleanSetting trapka = new BooleanSetting("Трапка", true);
    private final BooleanSetting plast = new BooleanSetting("Пласт", true);

    private int currentOutlineColor = 0xFFFFFFFF;
    private int targetOutlineColor = 0xFFFFFFFF;
    private float transitionTimer = 0.0f;
    private boolean lastPlayersInRadius = false;
    private static final float TRANSITION_DURATION = 0.5f;

    private Vec3 plastSmoothedCenter = Vec3.ZERO;
    private float plastSmoothedYawDeg = 0.0f;
    private float plastSmoothedPitchDeg = 0.0f;
    private boolean plastHasSmoothedPose = false;

    public ItemRadius() {
        super("ItemRadius", Category.RENDER);
        addSetting(dezka);
        addSetting(yavka);
        addSetting(fireCharge);
        addSetting(godsAura);
        addSetting(trapka);
        addSetting(plast);
    }

    public void onRender3D(PoseStack poseStack, MultiBufferSource bufferSource, float tickDelta) {
        if (!isEnabled()) return;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return;

        Player player = Minecraft.getInstance().player;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        boolean handled = false;

        if (dezka.get() && (isHolding(main, Items.ENDER_EYE) || isHolding(off, Items.ENDER_EYE))) {
            ItemStack eyeStack = isHolding(main, Items.ENDER_EYE) ? main : off;
            if (containsCyrillicLetter(eyeStack, 'Я')) {
                handled = true;
                float radius = 10.0f;
                Vec3 center = getCircleCenter(tickDelta);
                boolean playersInRadius = checkPlayersInRadius(player, center, radius);
                updateOutlineColor(playersInRadius, tickDelta);
                if (playersInRadius) {
                    renderCircleFill(poseStack, bufferSource, center, radius, 0x3300FF00, tickDelta);
                }
                renderCircleOutline(poseStack, bufferSource, center, radius, currentOutlineColor, tickDelta);
            }
        }

        if (!handled && yavka.get() && (isHolding(main, Items.SUGAR) || isHolding(off, Items.SUGAR))) {
            ItemStack sugarStack = isHolding(main, Items.SUGAR) ? main : off;
            if (containsCyrillicLetter(sugarStack, 'Я')) {
                handled = true;
                float radius = 10.0f;
                Vec3 center = getCircleCenter(tickDelta);
                boolean playersInRadius = checkPlayersInRadius(player, center, radius);
                updateOutlineColor(playersInRadius, tickDelta);
                if (playersInRadius) {
                    renderCircleFill(poseStack, bufferSource, center, radius, 0x3300FF00, tickDelta);
                }
                renderCircleOutline(poseStack, bufferSource, center, radius, currentOutlineColor, tickDelta);
            }
        }

        if (!handled && fireCharge.get() && (isHolding(main, Items.FIRE_CHARGE) || isHolding(off, Items.FIRE_CHARGE))) {
            ItemStack fireChargeStack = isHolding(main, Items.FIRE_CHARGE) ? main : off;
            if (containsCyrillicLetter(fireChargeStack, 'С')) {
                handled = true;
                float radius = 10.0f;
                Vec3 center = getCircleCenter(tickDelta);
                boolean playersInRadius = checkPlayersInRadius(player, center, radius);
                updateOutlineColor(playersInRadius, tickDelta);
                if (playersInRadius) {
                    renderCircleFill(poseStack, bufferSource, center, radius, 0x3300FF00, tickDelta);
                }
                renderCircleOutline(poseStack, bufferSource, center, radius, currentOutlineColor, tickDelta);
            }
        }

        if (!handled && godsAura.get() && (isHolding(main, Items.PHANTOM_MEMBRANE) || isHolding(off, Items.PHANTOM_MEMBRANE))) {
            ItemStack membraneStack = isHolding(main, Items.PHANTOM_MEMBRANE) ? main : off;
            if (containsCyrillicLetter(membraneStack, 'У')) {
                handled = true;
                float radius = 2.0f;
                Vec3 center = getCircleCenter(tickDelta);
                boolean playersInRadius = checkPlayersInRadius(player, center, radius);
                boolean friendsInRadius = checkFriendsInRadius(player, center, radius);
                updateOutlineColor(playersInRadius, tickDelta);
                if (friendsInRadius) {
                    renderCircleFill(poseStack, bufferSource, center, radius, 0x3300FF00, tickDelta);
                }
                renderCircleOutline(poseStack, bufferSource, center, radius, currentOutlineColor, tickDelta);
            }
        }

        if (!handled && trapka.get() && (isHolding(main, Items.NETHERITE_SCRAP) || isHolding(off, Items.NETHERITE_SCRAP))) {
            ItemStack scrapStack = isHolding(main, Items.NETHERITE_SCRAP) ? main : off;
            if (containsCyrillicLetter(scrapStack, 'А')) {
                handled = true;
                Vec3 p = getLerpedPlayerPos(tickDelta);
                Vec3 cubeCenter = new Vec3(
                        p.x,
                        p.y + 0.5 + 1.625,
                        p.z
                );
                boolean playersInRadius = checkPlayersInRadius(player, cubeCenter, 2.5);
                updateOutlineColor(playersInRadius, tickDelta);
                renderCubeOutline(poseStack, bufferSource, cubeCenter, 4.0f, currentOutlineColor, tickDelta);
            }
        }

        if (!handled && plast.get() && (isHolding(main, Items.DRIED_KELP) || isHolding(off, Items.DRIED_KELP))) {
            ItemStack kelpStack = isHolding(main, Items.DRIED_KELP) ? main : off;
            if (containsCyrillicLetter(kelpStack, 'П')) {
                handled = true;
                PlanePose pose = smoothPlastPose(computePlanePose(tickDelta), tickDelta);
                boolean playersInRadius = checkPlayersInRadius(player, pose.center, 2.5);
                if (playersInRadius) {
                    renderPlaneFill(poseStack, bufferSource, pose, 0x66FF0000, tickDelta);
                    renderPlaneOutline(poseStack, bufferSource, pose, 0xFFFF0000, tickDelta);
                } else {
                    renderPlaneOutline(poseStack, bufferSource, pose, 0xFF00FF00, tickDelta);
                }
            }
        }

        if (!handled) {
            plastHasSmoothedPose = false;
        }
    }

    private void renderCircleFill(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 center, float radius, int fillColor, float tickDelta) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        float y = (float) (center.y + player.getEyeHeight());
        float cx = (float) (center.x - cameraPos.x);
        float cy = (float) (y - cameraPos.y);
        float cz = (float) (center.z - cameraPos.z);

        int r = (fillColor >> 16) & 0xFF;
        int g = (fillColor >> 8) & 0xFF;
        int b = fillColor & 0xFF;
        int a = (fillColor >> 24) & 0xFF;

        int stepDeg = 5;
        float prevX = 0.0f;
        float prevZ = 0.0f;
        boolean hasPrev = false;

        poseStack.pushPose();
        
        for (int deg = 0; deg <= 360; deg += stepDeg) {
            double rad = Math.toRadians(deg);
            float x = cx + (float) (Math.sin(rad) * radius);
            float z = cz + (float) (-Math.cos(rad) * radius);

            if (!hasPrev) {
                prevX = x;
                prevZ = z;
                hasPrev = true;
                continue;
            }

            buffer.addVertex(poseStack.last().pose(), cx, cy, cz).setColor(r, g, b, a);
            buffer.addVertex(poseStack.last().pose(), prevX, cy, prevZ).setColor(r, g, b, a);
            buffer.addVertex(poseStack.last().pose(), x, cy, z).setColor(r, g, b, a);

            prevX = x;
            prevZ = z;
        }
        
        poseStack.popPose();
    }

    private PlanePose smoothPlastPose(PlanePose target, float tickDelta) {
        if (!plastHasSmoothedPose) {
            plastSmoothedCenter = target.center;
            plastSmoothedYawDeg = target.yawDeg;
            plastSmoothedPitchDeg = target.pitchDeg;
            plastHasSmoothedPose = true;
            return target;
        }

        float speed = 12.0f;
        float t = 1.0f - (float) Math.exp(-speed * Math.max(0.0f, tickDelta));

        plastSmoothedCenter = plastSmoothedCenter.add(target.center.subtract(plastSmoothedCenter).scale(t));
        plastSmoothedYawDeg = lerpAngleDeg(plastSmoothedYawDeg, target.yawDeg, t);
        plastSmoothedPitchDeg = lerpAngleDeg(plastSmoothedPitchDeg, target.pitchDeg, t);

        return new PlanePose(plastSmoothedCenter, plastSmoothedYawDeg, plastSmoothedPitchDeg);
    }

    private static float lerpAngleDeg(float from, float to, float t) {
        float delta = net.minecraft.util.Mth.wrapDegrees(to - from);
        return from + delta * t;
    }

    private void updateOutlineColor(boolean playersInRadius, float tickDelta) {
        if (playersInRadius != lastPlayersInRadius) {
            transitionTimer = 0.0f;
            lastPlayersInRadius = playersInRadius;
        }

        int baseOutline = 0xFFFFFFFF;
        int lightOutline = 0xFF00FF00;
        targetOutlineColor = playersInRadius ? lightOutline : baseOutline;

        float step = TRANSITION_DURATION <= 0.0001f ? 1.0f : (tickDelta / TRANSITION_DURATION);
        transitionTimer = Math.min(transitionTimer + step, 1.0f);
        currentOutlineColor = lerpColor(currentOutlineColor, targetOutlineColor, transitionTimer);
    }

    private static int lerpColor(int startColor, int endColor, float t) {
        int startA = (startColor >> 24) & 0xFF;
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endA = (endColor >> 24) & 0xFF;
        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int a = (int) (startA + (endA - startA) * t);
        int r = (int) (startR + (endR - startR) * t);
        int g = (int) (startG + (endG - startG) * t);
        int b = (int) (startB + (endB - startB) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static boolean isHolding(ItemStack stack, net.minecraft.world.item.Item item) {
        return stack != null && !stack.isEmpty() && stack.is(item);
    }

    private static boolean containsCyrillicLetter(ItemStack stack, char letter) {
        if (stack == null || stack.isEmpty()) return false;
        String name = stack.getHoverName().getString();
        if (name == null || name.isEmpty()) return false;
        String upper = name.toUpperCase();
        return upper.indexOf(Character.toUpperCase(letter)) >= 0;
    }

    private boolean checkPlayersInRadius(Player self, Vec3 centerPos, double radius) {
        if (Minecraft.getInstance().level == null) return false;
        double r2 = radius * radius;
        for (Player p : Minecraft.getInstance().level.players()) {
            if (p == null || p == self) continue;
            if (p.distanceToSqr(centerPos) <= r2) {
                return true;
            }
        }
        return false;
    }

    private boolean checkFriendsInRadius(Player self, Vec3 centerPos, double radius) {
        if (Minecraft.getInstance().level == null) return false;
        // Здесь можно добавить проверку друзей если есть система друзей
        // Пока просто проверяем всех игроков
        return checkPlayersInRadius(self, centerPos, radius);
    }

    private Vec3 getLerpedPlayerPos(float tickDelta) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return Vec3.ZERO;
        double x = player.xOld + (player.getX() - player.xOld) * tickDelta;
        double y = player.yOld + (player.getY() - player.yOld) * tickDelta;
        double z = player.zOld + (player.getZ() - player.zOld) * tickDelta;
        return new Vec3(x, y, z);
    }

    private Vec3 getCircleCenter(float tickDelta) {
        Vec3 pos = getLerpedPlayerPos(tickDelta);
        return pos.add(0.0, -1.4, 0.0);
    }

    private void renderCircleOutline(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 center, float radius, int outlineColor, float tickDelta) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        float y = (float) (center.y + player.getEyeHeight());
        float cx = (float) (center.x - cameraPos.x);
        float cy = (float) (y - cameraPos.y);
        float cz = (float) (center.z - cameraPos.z);

        int r = (outlineColor >> 16) & 0xFF;
        int g = (outlineColor >> 8) & 0xFF;
        int b = outlineColor & 0xFF;
        int a = (outlineColor >> 24) & 0xFF;

        int stepDeg = 5;
        float firstX = 0.0f;
        float firstZ = 0.0f;
        float prevX = 0.0f;
        float prevZ = 0.0f;
        boolean hasPrev = false;

        poseStack.pushPose();

        for (int deg = 0; deg <= 360; deg += stepDeg) {
            double rad = Math.toRadians(deg);
            float x = cx + (float) (Math.sin(rad) * radius);
            float z = cz + (float) (-Math.cos(rad) * radius);

            if (!hasPrev) {
                firstX = x;
                firstZ = z;
                prevX = x;
                prevZ = z;
                hasPrev = true;
                continue;
            }

            buffer.addVertex(poseStack.last().pose(), prevX, cy, prevZ).setColor(r, g, b, a);
            buffer.addVertex(poseStack.last().pose(), x, cy, z).setColor(r, g, b, a);

            prevX = x;
            prevZ = z;
        }

        if (hasPrev) {
            buffer.addVertex(poseStack.last().pose(), prevX, cy, prevZ).setColor(r, g, b, a);
            buffer.addVertex(poseStack.last().pose(), firstX, cy, firstZ).setColor(r, g, b, a);
        }

        poseStack.popPose();
    }

    private void renderPlaneFill(PoseStack poseStack, MultiBufferSource bufferSource, PlanePose pose, int fillColor, float tickDelta) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        float width = 4.0f;
        float height = 4.0f;
        float thickness = 1.5f;

        float halfW = width / 2.0f;
        float halfH = height / 2.0f;
        float halfT = thickness / 2.0f;

        poseStack.pushPose();
        poseStack.translate(pose.center.x - cameraPos.x, pose.center.y - cameraPos.y, pose.center.z - cameraPos.z);

        if (Math.abs(pose.pitchDeg) > 0.001f) {
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pose.pitchDeg));
        }
        if (Math.abs(pose.yawDeg) > 0.001f) {
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(pose.yawDeg));
        }

        float minX = -halfW;
        float maxX = halfW;
        float minY = -halfH;
        float maxY = halfH;
        float minZ = -halfT;
        float maxZ = halfT;

        drawBoxFaces(buffer, poseStack.last(), minX, minY, minZ, maxX, maxY, maxZ, fillColor);
        poseStack.popPose();
    }

    private static void drawBoxFaces(VertexConsumer buffer, PoseStack.Pose pose,
                                     float minX, float minY, float minZ,
                                     float maxX, float maxY, float maxZ,
                                     int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        buffer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a);

        buffer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a);

        buffer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a);

        buffer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a);

        buffer.addVertex(pose, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, minX, maxY, minZ).setColor(r, g, b, a);

        buffer.addVertex(pose, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(pose, maxX, maxY, maxZ).setColor(r, g, b, a);
    }

    private void renderCubeOutline(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 center, float size, int outlineColor, float tickDelta) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        float half = size / 2.0f;
        float minX = (float) (center.x - cameraPos.x - half);
        float minY = (float) (center.y - cameraPos.y - half);
        float minZ = (float) (center.z - cameraPos.z - half);
        float maxX = minX + size;
        float maxY = minY + size;
        float maxZ = minZ + size;

        drawBoxEdges(buffer, poseStack.last(), minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
    }

    private static void drawBoxEdges(VertexConsumer buffer, PoseStack.Pose pose,
                                     float minX, float minY, float minZ,
                                     float maxX, float maxY, float maxZ,
                                     int color) {
        drawLine(buffer, pose, minX, minY, minZ, maxX, minY, minZ, color);
        drawLine(buffer, pose, maxX, minY, minZ, maxX, minY, maxZ, color);
        drawLine(buffer, pose, maxX, minY, maxZ, minX, minY, maxZ, color);
        drawLine(buffer, pose, minX, minY, maxZ, minX, minY, minZ, color);

        drawLine(buffer, pose, minX, maxY, minZ, maxX, maxY, minZ, color);
        drawLine(buffer, pose, maxX, maxY, minZ, maxX, maxY, maxZ, color);
        drawLine(buffer, pose, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        drawLine(buffer, pose, minX, maxY, maxZ, minX, maxY, minZ, color);

        drawLine(buffer, pose, minX, minY, minZ, minX, maxY, minZ, color);
        drawLine(buffer, pose, maxX, minY, minZ, maxX, maxY, minZ, color);
        drawLine(buffer, pose, maxX, minY, maxZ, maxX, maxY, maxZ, color);
        drawLine(buffer, pose, minX, minY, maxZ, minX, maxY, maxZ, color);
    }

    private static void drawLine(VertexConsumer buffer, PoseStack.Pose pose,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
        buffer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a);
    }

    private record PlanePose(Vec3 center, float yawDeg, float pitchDeg) {
    }

    private PlanePose computePlanePose(float tickDelta) {
        Vec3 playerPos = getLerpedPlayerPos(tickDelta);
        Vec3 start = playerPos.add(0.0, Minecraft.getInstance().player.getEyeHeight(), 0.0);
        Vec3 lookVec = Minecraft.getInstance().player.getViewVector(tickDelta);
        Vec3 end = start.add(lookVec.scale(4.0));

        HitResult hit = Minecraft.getInstance().level.clip(new net.minecraft.world.level.ClipContext(
                start,
                end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                Minecraft.getInstance().player
        ));

        float pitch = Minecraft.getInstance().player.getXRot();
        boolean isLookingDown = pitch > 45.0f;
        boolean isLookingUp = pitch < -45.0f;
        boolean isLookingHorizontal = !isLookingDown && !isLookingUp;

        float thickness = 1.5f;
        float halfThickness = thickness / 2.0f;

        Vec3 center;
        float yawDeg;
        float pitchDeg;

        if (hit.getType() == HitResult.Type.BLOCK && hit.getLocation().distanceTo(start) <= 4.0) {
            Vec3 hitPos = hit.getLocation();
            net.minecraft.core.Direction face = ((net.minecraft.world.phys.BlockHitResult) hit).getDirection();

            if (isLookingDown) {
                center = new Vec3(
                        Math.floor(hitPos.x) + 0.5,
                        Math.floor(hitPos.y + 1.0) - 1.8 + halfThickness,
                        Math.floor(hitPos.z) + 0.5
                );
                yawDeg = 0.0f;
                pitchDeg = 90.0f;
            } else if (isLookingUp) {
                center = new Vec3(
                        Math.floor(hitPos.x) + 0.5,
                        Math.floor(hitPos.y) - halfThickness + 1.6,
                        Math.floor(hitPos.z) + 0.5
                );
                yawDeg = 0.0f;
                pitchDeg = -90.0f;
            } else {
                double offsetX = face.getStepX() != 0 ? face.getStepX() * halfThickness : 0.0;
                double offsetZ = face.getStepZ() != 0 ? face.getStepZ() * halfThickness : 0.0;
                center = new Vec3(
                        Math.floor(hitPos.x) + 0.5 + offsetX,
                        Math.floor(hitPos.y) + 0.5 + 1.6,
                        Math.floor(hitPos.z) + 0.5 + offsetZ
                );

                yawDeg = switch (face) {
                    case NORTH -> 180.0f;
                    case SOUTH -> 0.0f;
                    case WEST -> 90.0f;
                    case EAST -> -90.0f;
                    default -> -Minecraft.getInstance().player.getYRot();
                };
                pitchDeg = 0.0f;
            }
        } else {
            Vec3 approx = start.add(lookVec.scale(4.0));
            double y = Math.floor(approx.y) + (isLookingDown ? (-1.8 + halfThickness) : isLookingUp ? (-halfThickness + 1.6) : (0.5 + 1.6));
            center = new Vec3(Math.floor(approx.x) + 0.5, y, Math.floor(approx.z) + 0.5);
            yawDeg = -Minecraft.getInstance().player.getYRot();
            pitchDeg = 0.0f;
        }

        if (!isLookingHorizontal) {
            yawDeg = -Minecraft.getInstance().player.getYRot();
        }

        return new PlanePose(center, yawDeg, pitchDeg);
    }

    private void renderPlaneOutline(PoseStack poseStack, MultiBufferSource bufferSource, PlanePose pose, int outlineColor, float tickDelta) {
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        float width = 4.0f;
        float height = 4.0f;
        float thickness = 1.5f;

        float halfW = width / 2.0f;
        float halfH = height / 2.0f;
        float halfT = thickness / 2.0f;

        poseStack.pushPose();
        poseStack.translate(pose.center.x - cameraPos.x, pose.center.y - cameraPos.y, pose.center.z - cameraPos.z);

        if (Math.abs(pose.pitchDeg) > 0.001f) {
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pose.pitchDeg));
        }
        if (Math.abs(pose.yawDeg) > 0.001f) {
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(pose.yawDeg));
        }

        float minX = -halfW;
        float maxX = halfW;
        float minY = -halfH;
        float maxY = halfH;
        float minZ = -halfT;
        float maxZ = halfT;

        drawBoxEdges(buffer, poseStack.last(), minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
        poseStack.popPose();
    }
}
