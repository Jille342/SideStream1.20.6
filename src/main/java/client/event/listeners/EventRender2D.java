package client.event.listeners;

import client.event.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

/**
 * 2DRenderのEvent
 */
public class EventRender2D extends Event
{
	private Window resolution;
	private final float partialticks;
	DrawContext context;
	
	public EventRender2D(float partialticks, DrawContext context)
	{
		this.resolution = resolution;
		this.partialticks = partialticks;
		this.context = context;
	}
	
	public Window getResolution()
	{
		return resolution;
	}
	
	public DrawContext getContext()
	{
		return this.context;
	}
	
	public float getPartialTicks()
	{
		return partialticks;
	}
}
