package client.event.listeners;

import client.event.Event;

public class EventChat extends Event<EventChat>
{
	
	String message;
	
	public EventChat(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
}
