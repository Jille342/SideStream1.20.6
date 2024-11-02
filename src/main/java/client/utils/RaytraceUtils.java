package client.utils;

import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RaytraceUtils implements  MCUtil {
    public  EntityHitResult rayCastByRotation(float yaw, float pitch ,float reach) {
        ArrayList<EntityHitResult> targets1 = new ArrayList<EntityHitResult>();
        Entity entity = mc.getCameraEntity();

        if (entity != null && mc.world != null) {
            assert mc.player != null;
            float f = 1.0F;
            Vec3d eyeVec = entity.getCameraPosVec(1.0F);
            Vec3d lookVec = RotationUtils.getVectorForRotation(yaw, pitch);
            Vec3d vec32 = eyeVec.add(lookVec.x * (double) reach, lookVec.y * (double) reach,
                    lookVec.z * (double) reach);
                float f1 = 1.0f;
                Box box = entity.getBoundingBox().stretch(lookVec.multiply(reach)).expand(f1, f1, f1);
            return ProjectileUtil.raycast(entity,eyeVec, vec32,box,(e) -> !e.isSpectator() && e.canHit(), reach*reach);
        }
        return null;
    }
}
