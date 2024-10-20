package client.event.listeners;

import client.event.Event;

public class EventRotation extends Event<EventRotation>
{
	private float yaw, pitch;
	private float yawSpeed, pitchSpeed;
	
	public EventRotation(final float yaw, final float pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		this.yawSpeed = 180;
		this.pitchSpeed = 180;
	}
	
	public float getYaw()
	{
		return yaw;
	}
	
	public float getPitch()
	{
		return pitch;
	}
	
	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
}
