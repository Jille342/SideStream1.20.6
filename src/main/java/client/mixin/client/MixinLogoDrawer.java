package client.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer
{
	/**
	 * @author Jill
	 * @reason FuckOff
	 */
	@Inject(at = {@At("HEAD")},
		method = {"draw(Lnet/minecraft/client/gui/DrawContext;IFI)V"},
		cancellable = true)
	
	public void draw(DrawContext context, int screenWidth, float alpha, int y,
		CallbackInfo ci)
	{
		ci.cancel();
	}
}
