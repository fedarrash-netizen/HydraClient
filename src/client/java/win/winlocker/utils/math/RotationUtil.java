package win.winlocker.utils.math;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RotationUtil {

    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Получение углов для взгляда на цель
     */
    public static float[] getRotations(LivingEntity target) {
        if (target == null) {
            return new float[]{mc.player.getYRot(), mc.player.getXRot()};
        }

        Vec3 eyePos = new Vec3(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(), mc.player.getZ());
        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());

        double diffX = targetPos.x - eyePos.x;
        double diffY = targetPos.y - eyePos.y;
        double diffZ = targetPos.z - eyePos.z;

        double horizontalDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, horizontalDist));

        return new float[]{wrapAngle(yaw), clampPitch(pitch)};
    }

    /**
     * Получение углов с предиктом движения цели
     */
    public static float[] getPredictedRotations(LivingEntity target, int predictTicks) {
        if (target == null) {
            return new float[]{mc.player.getYRot(), mc.player.getXRot()};
        }

        Vec3 eyePos = new Vec3(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(), mc.player.getZ());
        
        // Предикт позиции цели
        double predictX = target.getX() + (target.getX() - target.xo) * predictTicks;
        double predictY = target.getY() + (target.getY() - target.yo) * predictTicks;
        double predictZ = target.getZ() + (target.getZ() - target.zo) * predictTicks;
        
        Vec3 targetPos = new Vec3(predictX, predictY + target.getEyeHeight(), predictZ);

        double diffX = targetPos.x - eyePos.x;
        double diffY = targetPos.y - eyePos.y;
        double diffZ = targetPos.z - eyePos.z;

        double horizontalDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, horizontalDist));

        return new float[]{wrapAngle(yaw), clampPitch(pitch)};
    }

    /**
     * Плавный поворот к целевым углам
     */
    public static void smoothRotate(float targetYaw, float targetPitch, float speed) {
        if (mc.player == null) return;

        float yawDiff = angleDifference(mc.player.getYRot(), targetYaw);
        float pitchDiff = angleDifference(mc.player.getXRot(), targetPitch);

        mc.player.setYRot(mc.player.getYRot() + yawDiff * speed);
        mc.player.setXRot(mc.player.getXRot() + pitchDiff * speed);
    }

    /**
     * Мгновенный поворот к целевым углам
     */
    public static void snapRotate(float targetYaw, float targetPitch) {
        if (mc.player == null) return;
        mc.player.setYRot(wrapAngle(targetYaw));
        mc.player.setXRot(clampPitch(targetPitch));
    }

    /**
     * Разница между углами с учетом wrap-around
     */
    public static float angleDifference(float current, float target) {
        float diff = target - current;
        while (diff > 180.0f) diff -= 360.0f;
        while (diff < -180.0f) diff += 360.0f;
        return diff;
    }

    /**
     * Wrap угла в диапазон [-180, 180]
     */
    public static float wrapAngle(float angle) {
        while (angle > 180.0f) angle -= 360.0f;
        while (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    /**
     * Clamp pitch в диапазон [-90, 90]
     */
    public static float clampPitch(float pitch) {
        return Math.max(-90.0f, Math.min(90.0f, pitch));
    }

    /**
     * Проверка видимости цели (raycast)
     */
    public static boolean isVisible(LivingEntity target) {
        if (mc.player == null || target == null) return false;
        return mc.player.hasLineOfSight(target);
    }

    /**
     * Получение угла до цели (для проверки range)
     */
    public static double getAngleToTarget(LivingEntity target) {
        float[] rotations = getRotations(target);
        float yawDiff = angleDifference(mc.player.getYRot(), rotations[0]);
        float pitchDiff = angleDifference(mc.player.getXRot(), rotations[1]);
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }
}
