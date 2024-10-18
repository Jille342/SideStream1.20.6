package client.mixin.client;

import client.ui.BackgroundManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen
{
	@Shadow
	public int width;
	
	@Shadow
	public int height;
	
	@Inject(at = {@At("HEAD")},
		method = {"renderPanoramaBackground"},
		cancellable = true)
	public void renderPanoramaBackground(DrawContext context, float delta,
		CallbackInfo ci)
	{
		ci.cancel();
		BackgroundManager.drawBackGround(context, width, height);
	}
	
	/**
	 * @author Jill
	 * @reason f
	 */
	@Inject(at = {@At("HEAD")},
		method = {"renderDarkening(Lnet/minecraft/client/gui/DrawContext;)V"},
		cancellable = true)
	
	public void renderDarkening(DrawContext context, CallbackInfo ci)
	{
		ci.cancel();
	}
	
	@Inject(at = {@At("HEAD")},
		method = {
			"renderDarkening(Lnet/minecraft/client/gui/DrawContext;IIII)V"},
		cancellable = true)
	public void renderDarkening(DrawContext context, int x, int y, int width,
		int height, CallbackInfo ci)
	{
		ci.cancel();
	}
	
	/**
	 * @author Jill
	 * @reason f
	 */
	@Inject(at = {@At("HEAD")},
		method = {"renderBackgroundTexture"},
		cancellable = true)
	private static void renderBackgroundTexture(DrawContext context,
		Identifier texture, int x, int y, float u, float v, int width,
		int height, CallbackInfo ci)
	{
		ci.cancel();
	}
	
	/**
	 * @author Jill
	 * @reason f
	 */
	@Inject(at = {@At("HEAD")},
		method = {"renderInGameBackground"},
		cancellable = true)
	public void renderInGameBackground(DrawContext context, CallbackInfo ci)
	{
		ci.cancel();
	}
	
	/**
	 * @author Jill
	 * @reason f
	 */
	@Inject(at = {@At("HEAD")}, method = {"applyBlur"}, cancellable = true)
	public void applyBlur(float delta, CallbackInfo ci)
	{
		ci.cancel();
	}
}
