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
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RotationUtils implements MCUtil{
	public static float virtualYaw, virtualPitch, virtualPrevYaw,
		virtualPrevPitch;
	public static final double DEG_TO_RAD = Math.PI / 180.0;

	public static final double RAD_TO_DEG = 180.0 / Math.PI;
	
	public RotationUtils()
	{}

	public static Vec3d getEyesPos()
	{
		assert mc.player != null;
		return new Vec3d(MinecraftClient.getInstance().player.getX(),
			mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
			mc.player.getZ());
	}
	public static float[] getAngleToBlockPos(final BlockPos pos)
	{
		return calcAngle(mc.player.getEyePos(), getNearestBlockPos(pos));
	}
	public static Vec3d getNearestBlockPos( BlockPos blockPos){
		Vec3d eye = Objects.requireNonNull(mc.player).getEyePos();
		Box bb = new Box(blockPos);
		Vec3d  newBlockPos  = new Vec3d(MathHelper.clamp(eye.x, bb.minX, bb.maxX),
				MathHelper.clamp(eye.y, bb.minY, bb.maxY),
				MathHelper.clamp(eye.z, bb.minZ, bb.maxZ));
		return newBlockPos;
	}

	public static float[] calcAngle(final Vec3d from, final Vec3d to) {
		final double difX = to.x - from.x;
		final double difY = (to.y - from.y) * -1.0;
		final double difZ = to.z - from.z;
		final double dist = Math.sqrt((float) (difX * difX + difZ * difZ));
		return new float[]{
				(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0),
				(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
	}

		public static Vec3d getVectorForRotation(float yaw, float pitch) {
		float var2 = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
		float var3 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
		float var4 = -MathHelper.cos(-pitch * 0.017453292F);
		float var5 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double)(var3 * var4), (double)var5, (double)(var2 * var4));
	}

	
	public static float limitAngleChange(float current, float intended,
		float maxChange)
	{
		float change = MathHelper.wrapDegrees(intended - current);
		
		change = MathHelper.clamp(change, -maxChange, maxChange);
		
		return MathHelper.wrapDegrees(current + change);
	}

	
	/**
	 * Returns the smallest angle difference possible with a specific
	 * sensitivity ("gcd")
	 */
	public static float getFixedAngleDelta()
	{
		// float z = (float) (mc.options.getMouseSensitivity().getValue() * 0.6f
		// + 0.2f);
		float z = (float)(0.1 * 0.6f + 0.2f);
		return(z * z * z * 1.2f);
	}
	
	/**
	 * Returns angle that is legitimately accomplishable with player's current
	 * sensitivity
	 */
	public static float getFixedSensitivityAngle(float targetAngle,
		float startAngle)
	{
		float gcd = getFixedAngleDelta();
		return startAngle + (int)((targetAngle - startAngle) / gcd) * gcd;
	}
	
	public static float[] getFixedSensitivityAngles(float[] targetAngles,
		float[] startAngles)
	{
		return new float[]{
			getFixedSensitivityAngle(targetAngles[0], startAngles[0]),
			getFixedSensitivityAngle(targetAngles[1], startAngles[1])};
		
	}
	
	public static void faceEntityClient(Entity entity)
	{
		// get position & rotation
		Vec3d eyesPos = getEyesPos();
		Vec3d lookVec = getVectorForRotation(virtualYaw, virtualPitch);
		
		// try to face center of boundingBox
		Box bb = entity.getBoundingBox();
		if(faceVectorClient(bb.getCenter()))
			return;
		
		// if not facing center, check if facing anything in boundingBox
		bb.intersects(eyesPos, eyesPos.add(lookVec.multiply(6)));
	}
	
	public static boolean faceVectorClient(Vec3d vec)
	{
		float[] rotations = getRotations(vec.getX(),vec.getY(),vec.getZ());
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

	private double limitAngleChange(double current, double intended, double speed) {
		double change = intended - current;
		if (change > speed) {
			change = speed;
		} else if (change < -speed) {
			change = -speed;
		}
		return current + change;
	}
	private static float getFoVDistance(final float yaw, final Entity e) {
		return ((Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f) : (Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f));
	}
	public static float[] getLimitedAngles(float[] serverSideAngles,float[] targetAngles  ,Entity target){
		return  new float[]{
				(float) RotationUtils.limitAngleChange(serverSideAngles[0], targetAngles[0], Math.max(10, getFoVDistance(serverSideAngles[0], target) * 0.8f))
				, (float) RotationUtils.limitAngleChange(serverSideAngles[1], targetAngles[1], 10 + new Random().nextInt(30))
		};
	}public static float[] getLimitedAngles(float[] serverSideAngles,float[] targetAngles  ,float fov){
		return  new float[]{
				(float) RotationUtils.limitAngleChange(serverSideAngles[0], targetAngles[0], Math.max(10, fov))
				, (float) RotationUtils.limitAngleChange(serverSideAngles[1], targetAngles[1], 10 + new Random().nextInt(30))
		};
	}
	
	public static float fovToEntity(Entity ent)
	{
		double x = ent.getX() - mc.player.getX();
		double z = ent.getZ() - mc.player.getZ();
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float)(yaw * -1.0D);
	}


	
	public float[] fixedSensitivity(float[] rotations, float sens)
	{
		float f = sens * 0.6F + 0.2F;
		float gcd = f * f * f * 1.2F;
		return new float[]{(rotations[0] - rotations[0] % gcd),
			(rotations[1] - rotations[1] % gcd)};
	}

	public float[] applySensitivityPatch(final float[] targetRotation, float[] previousRotation) {
		final float mouseSensitivity = (float) (mc.options.getMouseSensitivity().getValue() * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
		final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
		final float yaw = previousRotation[0] + (float) (Math.round((targetRotation[0] - previousRotation[0]) / multiplier) * multiplier);
		final float pitch = previousRotation[1] + (float) (Math.round((targetRotation[1] - previousRotation[1]) / multiplier) * multiplier);
		return new float[]{yaw, MathHelper.clamp(pitch, -90, 90)};
	}
	
	public static Vec3d nearest(Box box, Vec3d vec)
	{
		return new Vec3d(MathHelper.clamp(vec.x, box.minX, box.maxX),
			MathHelper.clamp(vec.y, box.minY, box.maxY),
			MathHelper.clamp(vec.z, box.minZ, box.maxZ));
	}

	public  float[] calcRotation(Entity entity ,float speed, float range, boolean instant, boolean silent, float[]serverSideAngles, float instantAimSpeed) {
		if (serverSideAngles == null) {
			serverSideAngles = new float[]{
					mc.player.getYaw(), mc.player.getPitch()
			};
		}
		float tickDelta = mc.getTickDelta();
		float currentYaw = silent ? serverSideAngles[0] : mc.player.getYaw(tickDelta);
		float currentPitch = silent ? serverSideAngles[1] : mc.player.getPitch(tickDelta);
		float aimSpeed = instant ? instantAimSpeed : speed;
		if (aimSpeed > 1) {
			aimSpeed = 1;
		}
		float aYaw = 0, aPitch = 0;
		Vec3d eye = Objects.requireNonNull(mc.player).getCameraPosVec(tickDelta);
		Box bb = entity.getBoundingBox();
		Vec3d nearest = nearest(bb, eye);
		final float[] center = rotation(nearest.add(RandomUtils.nextDouble(-0.0001f, 0.0001),
				RandomUtils.nextDouble(-0.0010f, 0.0001),
				RandomUtils.nextDouble(-0.0001f, 0.0001)), eye);
		EntityHitResult hitResult = RaytraceUtils.rayCastByRotation(currentYaw, currentPitch, range);
		if (hitResult != null && hitResult.getEntity() != mc.player && hitResult.getEntity() == entity) {

			aYaw = currentYaw + MathHelper.wrapDegrees(center[0] - currentYaw);
			aPitch = currentPitch + MathHelper.wrapDegrees(center[1] - currentPitch);
			return new float[]{
					lerp(currentYaw, aYaw, speed * 0.5f),
					lerp(currentPitch, aPitch, speed * 0.5f)
			};
		} else {
			float[] wrappedAngles = wrapAngleArray(currentYaw, currentPitch, center);
			float deltaH = Math.abs(currentYaw - wrappedAngles[0]);
			float deltaV = Math.abs(currentPitch - wrappedAngles[1]);
			float[] newSpeed = computeTurnSpeed(deltaH, deltaV, aimSpeed);
			return lerpArray(new float[]{currentYaw, currentPitch}, wrappedAngles, newSpeed[0], newSpeed[1]);
		}
	}



	private float[] computeTurnSpeed( float diffH, float diffV , float speed) {
		if(diffH<1) {
			diffH = 1;
		}
		if(diffV<1){
			diffV = 1;
		}
		 float coeDiffH = 0.024f;
		 float coeDiffV = 0.024f;
		 float min = 0.005f;
		final float turnSpeedH = (float) (coeDiffH* Math.log10(diffH)  +  speed);
		float turnSpeedV = (float) (coeDiffV* Math.log10(diffV)  +  speed);
		return new float[] {
				 Math.min(Math.max(Math.abs(turnSpeedH), min), 1),
				Math.min(Math.max(Math.abs(turnSpeedV), min), 1)
		};
	}
	public float[] calcRotation(Entity entity) {
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
	public  float[] wrapAngleArray(float playerYaw , float playerPitch, float[] targetAngle){
		float yaw = targetAngle[0];
		float pitch = targetAngle[1];
		final float finishedYaw =
				playerYaw + MathHelper.wrapDegrees(yaw - playerYaw);
		final float finishedPitch = playerPitch
				+ MathHelper.wrapDegrees(pitch - playerPitch);
		return new float[]{
				finishedYaw,finishedPitch
		};
	}

	private float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}
	private float[] lerpArray (float[]from, float[] target, float vSpeed, float hSpeed){
		return new float[] {
				lerp(from[0],target[0],vSpeed), lerp(from[1],target[1],hSpeed)
		};
	}
	public static float[] rotation(double x, double y, double z, double ax,
		double ay, double az)
	{
		final double diffX = x - ax, diffY = y - ay, diffZ = z - az;
		final float yaw =
			(float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F),
			pitch = (float)(-Math
				.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));
		return new float[]{yaw, pitch};
	}
	
	public static float[] rotation(Vec3d a, Vec3d b)
	{
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
