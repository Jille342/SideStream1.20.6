package client.mixin.client;

import client.Client;
import client.event.listeners.EventNameTag;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity>
{
	@Shadow
	@Final
	protected EntityRenderDispatcher dispatcher;

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
	{
		EventNameTag event = new EventNameTag(entity, matrices, vertexConsumers);
		Client.onEvent(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
	
}
