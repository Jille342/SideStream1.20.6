package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;

public class EventKey extends Event<EventKey>
{
	
	@Getter
	@Setter
	public int code;
	public int action;
	
	public EventKey(int code, String scancode, int action, int modifiers)
	{
		this.code = code;
		this.action = action;
	}

}
