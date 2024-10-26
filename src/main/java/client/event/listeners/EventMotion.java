package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

public class EventMotion extends Event<EventMotion>
{
	
	@Setter
	@Getter
	public double x, y, z;
	@Setter
	@Getter
	public float yaw, pitch;
	@Setter
	@Getter
	public boolean onGround;
	
	private double lastX, lastY, lastZ;
	public float lastYaw, lastPitch;
	public boolean lastOnGround;
	
	public boolean isModded()
	{
		return lastX != x || lastY != y || lastZ != z || lastYaw != yaw
			|| lastPitch != pitch || lastOnGround != onGround;
	}
	
	public EventMotion(double x, double y, double z, float yaw, float pitch,
		boolean onGround)
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		
		this.lastX = x;
		this.lastY = y;
		this.lastZ = z;
		this.lastYaw = yaw;
		this.lastPitch = pitch;
		this.lastOnGround = onGround;
	}

	public void setPosition(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float[] getServerSideAngles(){
		return new float[]{
				yaw,pitch
		};
	}
	
	public void setPosition(BlockPos pos)
	{
		this.x = pos.getX() + .5;
		this.y = pos.getY();
		this.z = pos.getZ() + .5;
	}
	
}
