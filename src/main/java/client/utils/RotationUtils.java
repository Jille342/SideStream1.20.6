/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone. If not, see <https://www.gnu.org/licenses/>.
 */

package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class RotationUtils
{
	public static float virtualYaw, virtualPitch, virtualPrevYaw,
		virtualPrevPitch;
	static MinecraftClient mc = MinecraftClient.getInstance();
	public static final double DEG_TO_RAD = Math.PI / 180.0;
	
	public static final double RAD_TO_DEG = 180.0 / Math.PI;
	
	private RotationUtils()
	{}
	
	private static float serverYaw;
	private static float serverPitch;
	
	public static Vec3d getEyesPos()
	{
		assert mc.player != null;
		return new Vec3d(MinecraftClient.getInstance().player.getX(),
			mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
			mc.player.getZ());
	}
	
	public static Vec3d getServerLookVec()
	{
		float f = MathHelper.cos(-serverYaw * 0.017453292F - (float)Math.PI);
		float f1 = MathHelper.sin(-serverYaw * 0.017453292F - (float)Math.PI);
		float f2 = -MathHelper.cos(-serverPitch * 0.017453292F);
		float f3 = MathHelper.sin(-serverPitch * 0.017453292F);
		return new Vec3d(f1 * f2, f3, f * f2);
	}
	
	private static float[] getNeededRotations(Vec3d vec)
	{
		Vec3d eyesPos = getEyesPos();
		
		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;
		
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
		
		return new float[]{MathHelper.wrapDegrees(yaw),
			MathHelper.wrapDegrees(pitch)};
	}
	
	public static float limitAngleChange(float current, float intended,
		float maxChange)
	{
		float change = MathHelper.wrapDegrees(intended - current);
		
		change = MathHelper.clamp(change, -maxChange, maxChange);
		
		return MathHelper.wrapDegrees(current + change);
	}
	
	public static Rotation wrapAnglesToRelative(Rotation current,
		Rotation target)
	{
		if(current.yawIsReallyClose(target))
		{
			return new Rotation(current.getYaw(), target.getPitch());
		}
		return target.subtract(current).normalize().add(current);
	}
	
	public static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest,
		Rotation current)
	{
		return wrapAnglesToRelative(current, calcRotationFromVec3d(orig, dest));
	}
	
	private static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest)
	{
		double[] delta = {orig.x - dest.x, orig.y - dest.y, orig.z - dest.z};
		double yaw = MathHelper.atan2(delta[0], -delta[2]);
		double dist = Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
		double pitch = MathHelper.atan2(delta[1], dist);
		return new Rotation((float)(yaw * RAD_TO_DEG),
			(float)(pitch * RAD_TO_DEG));
	}
	
	public static Vec3d calcVec3dFromRotation(Rotation rotation)
	{
		float f = MathHelper
			.cos(-rotation.getYaw() * (float)DEG_TO_RAD - (float)Math.PI);
		float f1 = MathHelper
			.sin(-rotation.getYaw() * (float)DEG_TO_RAD - (float)Math.PI);
		float f2 = -MathHelper.cos(-rotation.getPitch() * (float)DEG_TO_RAD);
		float f3 = MathHelper.sin(-rotation.getPitch() * (float)DEG_TO_RAD);
		return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
	}

	/**
	 * Returns the smallest angle difference possible with a specific sensitivity ("gcd")
	 */
	public static float getFixedAngleDelta() {
//        float z = (float) (mc.options.getMouseSensitivity().getValue() * 0.6f + 0.2f);
		float z = (float) (0.1 * 0.6f + 0.2f);
		return (z * z * z * 1.2f);
	}

	/**
	 * Returns angle that is legitimately accomplishable with player's current sensitivity
	 */
	public static float getFixedSensitivityAngle(float targetAngle, float startAngle) {
		float gcd = getFixedAngleDelta();
		return startAngle + (int) ((targetAngle - startAngle) / gcd) * gcd;
	}
	public static float[] getFixedSensitivityAngles(float[] targetAngles, float[] startAngles){
		return new float[]{
				getFixedSensitivityAngle(targetAngles[0], startAngles[0]),getFixedSensitivityAngle(targetAngles[1], startAngles[1])
		};

	}

	
	public static boolean faceEntityClient(Entity entity)
	{
		// get position & rotation
		Vec3d eyesPos = getEyesPos();
		Vec3d lookVec = getServerLookVec();
		
		// try to face center of boundingBox
		Box bb = entity.getBoundingBox();
		if(faceVectorClient(bb.getCenter()))
			return true;
		
		// if not facing center, check if facing anything in boundingBox
		return bb.intersects(eyesPos, eyesPos.add(lookVec.multiply(6)));
	}
	
	public static boolean faceVectorClient(Vec3d vec)
	{
		float[] rotations = getNeededRotations(vec);
		
		float oldYaw = mc.player.prevYaw;
		float oldPitch = mc.player.prevPitch;
		
		mc.player.bodyYaw = limitAngleChange(oldYaw, rotations[0], 30);
		mc.player.setPitch(rotations[1]);
		
		return Math.abs(oldYaw - rotations[0])
			+ Math.abs(oldPitch - rotations[1]) < 1F;
	}
	
	public static boolean fov(Entity entity, double fov)
	{
		fov = (fov * 0.5);
		double v = ((double)(mc.player.getYaw() - fovToEntity(entity)) % 360.0D
			+ 540.0D) % 360.0D - 180.0D;
		return v > 0.0D && v < fov || -fov < v && v < 0.0D;
	}
	
	public static float[] getRotationsRandom(LivingEntity entity)
	{
		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		double randomXZ = threadLocalRandom.nextDouble(-0.08, 0.08);
		double randomY = threadLocalRandom.nextDouble(-0.125, 0.125);
		double x = entity.getX() + randomXZ;
		double y = entity.getY()
			+ (entity.getEyeHeight(entity.getPose()) / 2.05) + randomY;
		double z = entity.getZ() + randomXZ;
		return getRotations(x, y, z);
	}
	
	public static float[] getRotationsEntity(LivingEntity entity)
	{
		return PlayerHelper.isMoving() ? getRotations(
			entity.getX()
				+ ThreadLocalRandom.current().nextDouble(-0.03D, 0.03D),
			entity.getY() + (double)entity.getEyeHeight(entity.getPose()) - 0.4D
				+ ThreadLocalRandom.current().nextDouble(-0.07D, 0.07D),
			entity.getZ()
				+ ThreadLocalRandom.current().nextDouble(-0.03D, 0.03D))
			: getRotations(
				entity.getX(), entity.getY()
					+ (double)entity.getEyeHeight(entity.getPose()) - 0.4D,
				entity.getZ());
	}
	
	public static float fovToEntity(Entity ent)
	{
		double x = ent.getX() - mc.player.getX();
		double z = ent.getZ() - mc.player.getZ();
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float)(yaw * -1.0D);
	}
	
	public static float[] fixedSensitivity(float[] rotations, float sens)
	{
		float f = sens * 0.6F + 0.2F;
		float gcd = f * f * f * 1.2F;
		return new float[]{(rotations[0] - rotations[0] % gcd),
			(rotations[1] - rotations[1] % gcd)};
	}
	public static Vec3d nearest(Box box, Vec3d vec) {
		return new Vec3d(
				MathHelper.clamp(vec.x, box.minX, box.maxX),
				MathHelper.clamp(vec.y, box.minY, box.maxY),
				MathHelper.clamp(vec.z, box.minZ, box.maxZ)
		);
	}

	public static float[] calcRotation(Entity entity) {
		float aYaw = 0, aPitch = 0;
		 long next = 0;
		Vec3d eye = Objects.requireNonNull(mc.player).getEyePos();
		Box bb = entity.getBoundingBox();
		Vec3d nearest = nearest(bb, eye);
		if (bb.intersects(eye, eye.add(mc.player.getRotationVec(1f).multiply(6)))) {
			if (System.currentTimeMillis() > next) {
				final float[] center = rotation(entity.getEyePos().add(0, -0.3, 0), eye);
				next = System.currentTimeMillis() + RandomUtils.nextInt(50);
				aYaw = RandomUtils.nextFloat(0.3f) * MathHelper.wrapDegrees(
						center[0] - mc.player.getYaw()
				);
				aPitch = RandomUtils.nextFloat(0.3f) * MathHelper.wrapDegrees(
						center[1] - mc.player.getPitch()
				);
			}
			return new float[] {
					mc.player.getYaw() + aYaw * RandomUtils.nextFloat(1),
					mc.player.getPitch() + aPitch * RandomUtils.nextFloat(1)
			};
		}
        return rotation(nearest.add(
                RandomUtils.nextDouble(-0.1f, 0.1),
                RandomUtils.nextDouble(-0.1f, 0.1),
                RandomUtils.nextDouble(-0.1f, 0.1)
        ), eye);
	}
	public static float[] rotation(double x, double y, double z, double ax, double ay, double az) {
		final double diffX = x - ax, diffY = y - ay, diffZ = z - az;
		final float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F),
				pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
		return new float[] { yaw, pitch };
	}

	public static float[] rotation(Vec3d a, Vec3d b) {
		return rotation(a.x, a.y, a.z, b.x, b.y, b.z);
	}
	
	public static float[] getRotations(double posX, double posY, double posZ)
	{
		PlayerEntity player = mc.player;
		double x = posX - player.getX();
		double y = posY - (player.getY()
			+ (double)player.getEyeHeight(mc.player.getPose()));
		double z = posZ - player.getZ();
		double dist = (double)MathHelper.sqrt((float)(x * x + z * z));
		float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(-(Math.atan2(y, dist) * 180.0D / Math.PI));
		final float finishedYaw =
			player.getYaw() + MathHelper.wrapDegrees(yaw - player.getYaw());
		final float finishedPitch = player.getPitch()
			+ MathHelper.wrapDegrees(pitch - player.getPitch());
		return new float[]{finishedYaw, finishedPitch};
	}
	
	public static float calculateYawChangeToDst(Entity entity)
	{
		double diffX = entity.getX() - mc.player.getX();
		double diffZ = entity.getZ() - mc.player.getZ();
		double deg = Math.toDegrees(Math.atan(diffZ / diffX));
		if(diffZ < 0.0 && diffX < 0.0)
		{
			return (float)MathHelper
				.wrapDegrees(-(mc.player.getYaw() - (90 + deg)));
		}else if(diffZ < 0.0 && diffX > 0.0)
		{
			return (float)MathHelper
				.wrapDegrees(-(mc.player.getYaw() - (-90 + deg)));
		}else
		{
			return (float)MathHelper.wrapDegrees(-(mc.player.getYaw()
				- Math.toDegrees(-Math.atan(diffX / diffZ))));
		}
	}
}
