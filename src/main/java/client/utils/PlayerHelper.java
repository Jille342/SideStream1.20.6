package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;


public class PlayerHelper implements  MCUtil{

    public static void legitAttack() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.crosshairTarget == null || mc.player.isRiding()) {
            return;
        }

        switch (mc.crosshairTarget.getType()) {
            case ENTITY:
                mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                break;

            case BLOCK:

            case MISS:
        }

        mc.player.swingHand(Hand.MAIN_HAND);
    }
    public static boolean fov(Entity entity, double fov) {
        fov = (fov * 0.5);
        double v = ((double)(mc.player.bodyYaw - fovToEntity(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
        return v > 0.0D && v < fov || -fov < v && v < 0.0D;
    }
    public static float fovToEntity(Entity ent) {
        double x = ent.getX() - mc.player.getX();
        double z = ent.getZ() - mc.player.getZ();
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float)(yaw * -1.0D);
    }
    public static boolean isMoving() {
        return mc.player.forwardSpeed != 0.0F || mc.player.sidewaysSpeed != 0.0F;
    }
}
