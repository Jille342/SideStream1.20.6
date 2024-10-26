package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventChat extends Event<EventChat>
{
	
	String message;
	
	public EventChat(String message)
	{
		this.message = message;
	}

}
