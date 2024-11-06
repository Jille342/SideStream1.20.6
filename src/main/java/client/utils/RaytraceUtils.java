package client.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RaytraceUtils implements  MCUtil {
    public static EntityHitResult rayCastByRotation(float yaw, float pitch ,float reach) {
        Entity cameraEntity = mc.getCameraEntity();
        double e = MathHelper.square(reach);
        if (cameraEntity != null && mc.world != null) {
            assert mc.player != null;
            float f = 1.0F;
            Vec3d eyeVec = cameraEntity.getCameraPosVec(1.0F);
            Vec3d lookVec = RotationUtils.getVectorForRotation(yaw, pitch);
            Vec3d vec32 = eyeVec.add(lookVec.x * (double) reach, lookVec.y * (double) reach,
                    lookVec.z * (double) reach);
                float f1 = 1.0f;
                Box box = cameraEntity.getBoundingBox().stretch(lookVec.multiply(reach)).expand(f1, f1, f1);
         return  ProjectileUtil.raycast(cameraEntity, eyeVec, vec32, box, (entity) -> !entity.isSpectator() && entity.canHit(), e);
        }
        return null;
    }
}
