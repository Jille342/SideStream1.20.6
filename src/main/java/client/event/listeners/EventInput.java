package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.Input;

public class EventInput extends Event<EventInput>
{
	@Getter
	private final Input input;
	
	@Getter
	@Setter
	private float slowDownFactor;
	public boolean moveFix = false;
	
	public EventInput(Input input, float slowDownFactor)
	{
		this.input = input;
		this.slowDownFactor = slowDownFactor;
	}
	
}
