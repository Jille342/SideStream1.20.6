package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;

@Getter
public class EventRenderWorld extends Event<EventRenderWorld>
{
	
	@Setter
	float partialTicks;
	MatrixStack stack;
	
	public EventRenderWorld(float partialTicks, MatrixStack stack)
	{
		this.partialTicks = partialTicks;
		this.stack = stack;
	}
	
}
