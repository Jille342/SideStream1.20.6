package client.event;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event<T>
{
	
	public boolean cancelled;
	public EventType type;
	public EventDirection direction;
	
	public void cancel()
	{
		this.cancelled = true;
	}
	
	public boolean isPre()
	{
		if(type == null)
			return false;
		
		return type == EventType.PRE;
	}
	
	public boolean isPost()
	{
		if(type == null)
			return false;
		
		return type == EventType.POST;
	}
	
	public boolean isIncoming()
	{
		if(direction == null)
			return false;
		
		return direction == EventDirection.INCOMING;
	}
	
	public boolean isOutgoing()
	{
		if(direction == null)
			return false;
		
		return direction == EventDirection.OUTGOING;
	}
	
}
