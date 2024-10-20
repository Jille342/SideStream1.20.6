package client.mixin.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity>
{
	@Shadow
	@Final
	protected EntityRenderDispatcher dispatcher;
	
	@Shadow
	public abstract TextRenderer getTextRenderer();
	
	/*
	 * @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable =
	 * true)
	 * protected void renderLabelIfPresent(T entity, Text text, MatrixStack
	 * matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo
	 * ci) {
	 * NametagsMod mod = Client.modManager().getMod(NametagsMod.class);
	 * if(!mod.isEnabled()) return;
	 * ci.cancel();
	 *
	 * double d = this.dispatcher.getSquaredDistanceToCamera(entity);
	 * if (!(d > 4096.0)) {
	 * boolean bl = !entity.isSneaky();
	 * float f = entity.getHeight() + 0.5F;
	 * int i = "deadmau5".equals(text.getString()) ? -10 : 0;
	 * matrices.push();
	 * matrices.translate(0.0F, f, 0.0F);
	 * matrices.multiply(this.dispatcher.getRotation());
	 * matrices.scale(-0.025F, -0.025F, 0.025F);
	 * Matrix4f matrix4f = matrices.peek().getPositionMatrix();
	 * float g =
	 * MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
	 * int j = (int)(g * 255.0F) << 24;
	 * TextRenderer textRenderer = this.getTextRenderer();
	 * float h = (float)(-textRenderer.getWidth(text) / 2);
	 * JColor empty = new JColor(0f,0f,0f, 0f);
	 * j = (int)(mod.opacity.getFValue() * 255.0F) << 24;
	 * if(mod.textShadow.isEnabled()) {
	 * j = (int)(mod.opacity.getFValue()/2 * 255.0F) << 24;
	 * textRenderer.draw(text, h, (float) i, 553648127, true, matrix4f,
	 * vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH :
	 * TextRenderer.TextLayerType.NORMAL, j, light);
	 * if (bl) {
	 * textRenderer.draw(text, h, (float) i, -1, true, matrix4f,
	 * vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
	 * }
	 * textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f,
	 * vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH :
	 * TextRenderer.TextLayerType.NORMAL, 0, light);
	 * if (entity instanceof AbstractClientPlayerEntity &&
	 * text.getString().contains(entity.getName().getString()))
	 * IndicatorHelper.addBadge(entity, matrices, vertexConsumers);
	 *
	 * if (bl) {
	 * textRenderer.draw(text, h, (float) i, -1, false, matrix4f,
	 * vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
	 * }
	 * } else {
	 * textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f,
	 * vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH :
	 * TextRenderer.TextLayerType.NORMAL, j, light);
	 * if (entity instanceof AbstractClientPlayerEntity &&
	 * text.getString().contains(entity.getName().getString()))
	 * IndicatorHelper.addBadge(entity, matrices, vertexConsumers);
	 *
	 * if (bl) {
	 * textRenderer.draw(text, h, (float) i, -1, false, matrix4f,
	 * vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
	 * }
	 * }
	 *
	 * matrices.pop();
	 * }
	 * }
	 */
	
}
