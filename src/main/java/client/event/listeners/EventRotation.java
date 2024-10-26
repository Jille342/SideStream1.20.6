package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;

public class EventRotation extends Event<EventRotation>
{
	@Setter
	@Getter
	private float yaw, pitch;
	private float yawSpeed, pitchSpeed;
	
	public EventRotation(final float yaw, final float pitch)
	{
		this.yaw = yaw;
		this.pitch = pitch;
		this.yawSpeed = 180;
		this.pitchSpeed = 180;
	}
}
