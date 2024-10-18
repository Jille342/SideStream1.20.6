package client.event.listeners;

import client.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRenderWorld extends Event<EventRenderWorld>
{
	
	float partialTicks;
	MatrixStack stack;
	
	public EventRenderWorld(float partialTicks, MatrixStack stack)
	{
		this.partialTicks = partialTicks;
		this.stack = stack;
	}
	
	public float getPartialTicks()
	{
		return partialTicks;
	}
	
	public MatrixStack getStack()
	{
		return stack;
	}
	
	public void setPartialTicks(float partialTicks)
	{
		this.partialTicks = partialTicks;
	}
}
