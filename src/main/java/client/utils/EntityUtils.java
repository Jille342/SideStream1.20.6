package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtils implements  MCUtil{
    public static boolean isTeammate(PlayerEntity player)
    {
        return player.isTeammate(MinecraftClient.getInstance().player);
    }
    public static boolean isInFov(Entity entity, double fov)
    {
        fov = (fov * 0.5);
        assert mc.player != null;
        double v = ((double)(mc.player.bodyYaw - fovToEntity(entity)) % 360.0D
                + 540.0D) % 360.0D - 180.0D;
        return (!(v > 0.0D) || !(v < fov)) && (!(-fov < v) || !(v < 0.0D));
    }

    public static float fovToEntity(Entity ent)
    {
        assert mc.player != null;
        double x = ent.getX() - mc.player.getX();
        double z = ent.getZ() - mc.player.getZ();
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float)(yaw * -1.0D);
    }
    public static  Vec3d getInterpolatedEntityPos(Entity entity, float partialTicks){
        double interpolatedX = MathHelper.lerp(partialTicks,
                entity.prevX, entity.getX());
        double interpolatedY = MathHelper.lerp(partialTicks,
                entity.prevY, entity.getY());
        double interpolatedZ = MathHelper.lerp(partialTicks,
                entity.prevZ, entity.getZ());
        return  new Vec3d(interpolatedX, interpolatedY, interpolatedZ);
    }

    public static Box getEntityBox(Entity entity, float partialTicks){
        Vec3d interpolatedPos = getInterpolatedEntityPos(entity, partialTicks);
        Box boundingBox = entity.getBoundingBox().offset(
                interpolatedPos.getX() - entity.getX(),
                interpolatedPos.getY() - entity.getY(),
                interpolatedPos.getZ() - entity.getZ());
    return boundingBox;
    }

}
