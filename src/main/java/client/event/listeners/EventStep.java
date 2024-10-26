package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventStep extends Event<EventStep>
{
	
	double height;
	boolean canStep;
	
	public EventStep(double height, boolean canStep)
	{
		this.height = height;
		this.canStep = canStep;
	}

}
