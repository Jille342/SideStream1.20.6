package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;

public class NoHurtcam extends Module
{
	public NoHurtcam()
	{
		super("NoHurtcam", 0, Category.RENDER);
	}
	
	double lastGamma;
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			
		}
		super.onEvent(e);
	}
	
	@Override
	public void onEnable()
	{
		
		super.onEnable();
	}
	
	@Override
	public void onDisable()
	{
		super.onDisable();
	}
	
}
