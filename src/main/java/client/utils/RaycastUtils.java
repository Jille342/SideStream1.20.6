package client.utils;

import net.minecraft.util.hit.HitResult;

public class RaycastUtils implements MCUtil
{
	
	public static HitResult rayCast(float[] rot, double dist, float delta)
	{
		float prevYaw = mc.player.prevYaw, prevPitch = mc.player.prevPitch,
			yaw = mc.player.getYaw(), pitch = mc.player.getPitch();
		mc.player.setYaw(rot[0]);
		mc.player.setPitch(rot[1]);
		mc.player.prevYaw = yaw;
		mc.player.prevPitch = pitch;
		HitResult result = mc.player.raycast(dist, delta, false);
		mc.player.setYaw(yaw);
		mc.player.setPitch(pitch);
		mc.player.prevPitch = prevPitch;
		mc.player.prevYaw = prevYaw;
		return result;
	}
}
