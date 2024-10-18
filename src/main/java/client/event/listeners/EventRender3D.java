package client.event.listeners;

import client.event.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

/**
 * 2DRenderのEvent
 */
public class EventRender3D extends Event {
    MatrixStack matrices;
    float partialTicks;
Frustum frustum;
    public EventRender3D(MatrixStack matrix4f,  float partialTicks, Frustum frustum) {
        this.matrices = matrix4f;
        this.partialTicks = partialTicks;
        this.frustum = frustum;
    }

    public MatrixStack getMatrix() {
        return matrices;
    }
    public float getPartialTicks() {
        return partialTicks;
    }
    public Frustum getFrustum(){
        return frustum;
    }
}