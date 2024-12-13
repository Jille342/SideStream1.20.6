package client.utils;

import client.event.listeners.EventMotion;
import client.event.listeners.EventMove;
import net.minecraft.entity.effect.StatusEffects;

public class MoveUtils implements MCUtil{

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            final int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    public static boolean isOnGround(double height) {
        return mc.world.getEntityCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, height, 0.0D)).isEmpty();
    }
    public static int getSpeedEffect() {
        if (mc.player.hasStatusEffect(StatusEffects.SPEED))
            return mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1;
        else
            return 0;
    }
    public static boolean isMoving()
    {
        return mc.player.forwardSpeed != 0.0F
                || mc.player.sidewaysSpeed != 0.0F;
    }
    public static double getSpeed() {
        return mc.player.getVelocity().horizontalLength();
    }
    public static double getDirection(float yaw) {
        float rotationYaw = yaw;
        if (mc.options.backKey.isPressed()) {
            rotationYaw = yaw + 180.0F;
        }

        float forward = 1.0F;
        if (mc.options.backKey.isPressed()) {
            forward = -0.5F;
        } else if (mc.options.forwardKey.isPressed()) {
            forward = 0.5F;
        }

        if (mc.options.leftKey.isPressed()) {
            rotationYaw -= 90.0F * forward;
        }

        if (mc.options.rightKey.isPressed()) {
            rotationYaw += 90.0F * forward;
        }

        return Math.toRadians((double)rotationYaw);
    }


    public static int getJumpEffect() {
        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
            return mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1;
        else
            return 0;
    }

    public static void setMotion(EventMove event, double speed) {
        float forward = mc.player.input.movementForward;
        float side = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta();

        double velX, velZ;

        if (forward == 0.0f && side == 0.0f) {
            velX = 0; velZ = 0;
        }

        else if (forward != 0.0f) {
            if (side >= 1.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
                side = 0.0f;
            } else if (side <= -1.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
                side = 0.0f;
            }

            if (forward > 0.0f)
                forward = 1.0f;

            else if (forward < 0.0f)
                forward = -1.0f;
        }

        double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        double mz = Math.sin(Math.toRadians(yaw + 90.0f));

        velX = (double) forward * speed * mx + (double) side * speed * mz;
        velZ = (double) forward * speed * mz - (double) side * speed * mx;

        event.setX(velX);
            event.setZ(velZ);
        }
}
