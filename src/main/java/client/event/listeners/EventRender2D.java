package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

/**
 * 2DRenderのEvent
 */
@Getter
public class EventRender2D extends Event {
    private final float partialTicks;
    DrawContext context;
    private Window resolution;

    public EventRender2D(float partialTicks, DrawContext context) {
        this.resolution = resolution;
        this.partialTicks = partialTicks;
        this.context = context;
    }

}
