package client.mixin.client;

import client.features.modules.ModuleManager;
import client.Client;
import client.event.listeners.EventRender2D;
import client.features.modules.render.HUD;
import client.utils.TickManager;
import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud
{
	@Unique
	private static final int MAX_SAMPLES =
		GL30.glGetInteger(GL30C.GL_MAX_SAMPLES);
	
	@Inject(method = "renderHotbar", at = @At("HEAD"))
	private void renderHotbar(DrawContext context, float tickDelta,
		CallbackInfo ci)
	{
		if(ModuleManager.getModulebyClass(HUD.class).enable)
			Client.hud2.draw(context);
	}
	
	@Inject(at = @At("HEAD"), method = {"render"})
	private void onRender(DrawContext context, float tickDelta, CallbackInfo ci)
	{
		final EventRender2D eventRender2D =
			new EventRender2D(tickDelta, context);
		Client.onEvent(eventRender2D);
		MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES),
			() -> TickManager.render(eventRender2D));
		
	}
	
	@Inject(method = "renderStatusEffectOverlay",
		at = @At("HEAD"),
		cancellable = true)
	public void FuckEffects(DrawContext context, float tickDelta,
		CallbackInfo ci)
	{
		if(ModuleManager.getModulebyClass(HUD.class).isEnabled())
		{
			ci.cancel();
		}
	}
}
