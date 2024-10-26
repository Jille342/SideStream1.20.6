package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;

/**
 * 2DRenderのEvent
 */
@Getter
public class EventRender3D extends Event
{
	MatrixStack matrix;
	float partialTicks;
	Frustum frustum;
	
	public EventRender3D(MatrixStack matrix4f, float partialTicks,
		Frustum frustum)
	{
		this.matrix = matrix4f;
		this.partialTicks = partialTicks;
		this.frustum = frustum;
	}

}
