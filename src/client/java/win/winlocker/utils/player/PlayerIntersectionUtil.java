package win.winlocker.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Predicate;

/**
 * Утилиты для проверки пересечений игрока с блоками
 */
public class PlayerIntersectionUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Проверить, пересекается ли бокс с коллизиями
     */
    public static boolean isBox(AABB box, Predicate<BlockPos> collisionCheck) {
        if (mc.level == null || mc.player == null) {
            return false;
        }

        int minX = (int) Math.floor(box.minX);
        int minY = (int) Math.floor(box.minY);
        int minZ = (int) Math.floor(box.minZ);
        int maxX = (int) Math.floor(box.maxX);
        int maxY = (int) Math.floor(box.maxY);
        int maxZ = (int) Math.floor(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (collisionCheck.test(pos)) {
                        BlockState state = mc.level.getBlockState(pos);
                        VoxelShape shape = state.getCollisionShape(mc.level, pos, CollisionContext.of(mc.player));
                        
                        if (!shape.isEmpty()) {
                            AABB shapeBox = shape.bounds().move(pos.getX(), pos.getY(), pos.getZ());
                            if (box.intersects(shapeBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Проверить, есть ли коллизия в позиции
     */
    public static boolean hasCollision(BlockPos pos) {
        if (mc.level == null) {
            return false;
        }

        BlockState state = mc.level.getBlockState(pos);
        return !state.getCollisionShape(mc.level, pos).isEmpty();
    }

    /**
     * Проверить, есть ли блок в позиции
     */
    public static boolean isBlock(BlockPos pos) {
        if (mc.level == null) {
            return false;
        }

        BlockState state = mc.level.getBlockState(pos);
        return !state.isAir() && !state.getFluidState().isEmpty();
    }

    /**
     * Проверить, можно ли пройти через позицию
     */
    public static boolean canPassThrough(BlockPos pos) {
        if (mc.level == null) {
            return true;
        }

        BlockState state = mc.level.getBlockState(pos);
        VoxelShape shape = state.getCollisionShape(mc.level, pos, CollisionContext.of(mc.player));
        return shape.isEmpty();
    }

    /**
     * Raycast проверка
     */
    public static boolean raycastCheck(Vec3 start, Vec3 end, Predicate<BlockPos> predicate) {
        if (mc.level == null) {
            return false;
        }

        ClipContext context = new ClipContext(
            start,
            end,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            CollisionContext.of(mc.player)
        );

        var result = mc.level.clip(context);
        if (result.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return predicate.test(result.getBlockPos());
        }

        return false;
    }
}
