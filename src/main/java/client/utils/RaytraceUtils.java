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
    public  static java.util.List<EntityHitResult> rayCastByRotation(float yaw, float pitch ,float reach) {
        ArrayList<EntityHitResult> targets1 = new ArrayList<EntityHitResult>();
        Entity entity = mc.getCameraEntity();

        if (entity != null && mc.world != null) {
            assert mc.player != null;
            float f = 1.0F;
            Vec3d eyeVec = entity.getCameraPosVec(1.0F);
            Vec3d lookVec = RotationUtils.getVectorForRotation(yaw, pitch);
            Vec3d vec32 = eyeVec.add(lookVec.x * (double) reach, lookVec.y * (double) reach,
                    lookVec.z * (double) reach);


            for (Entity entity1 : mc.world.getEntities()) {
                float f1 = entity1.getTargetingMargin();
                Box box = entity.getBoundingBox().stretch(lookVec.multiply(reach)).expand(f1, f1, f1);
                EntityHitResult entityHitResults = ProjectileUtil.raycast(entity,eyeVec, vec32,box,(e) -> !e.isSpectator() && e.canHit(), reach*reach);
                if (entityHitResults != null) {
                    targets1.add(new EntityHitResult(entity1, entityHitResults.getPos()) {
                    });
                }
            }
        }
        if (entity != null) {
            targets1.sort((o1, o2) -> {
                Vec3d eyeVec = entity.getCameraPosVec(1.0F);
                return (int) ((eyeVec.distanceTo(o1.getPos()) - eyeVec.distanceTo(o2.getPos())) * 100.0);
            });
        }
        return targets1;
    }
}
