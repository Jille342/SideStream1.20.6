package client.mixin.client;

import client.ui.BackgroundManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatingCubeMapRenderer.class)
public class MixinRotatingCubeMapRenderer
{
	
	@Inject(at = {@At("HEAD")}, method = {"render"}, cancellable = true)
	public void render(DrawContext context, int width, int height, float alpha,
		float tickDelta, CallbackInfo ci)
	{
		ci.cancel();
		BackgroundManager.drawBackGround(context, width, height);
	}
}
