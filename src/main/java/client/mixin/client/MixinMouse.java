package client.mixin.client;

import client.Client;
import client.event.listeners.EventRotation;
import client.utils.RotationUtils;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MixinMouse
{
	
	@ModifyArgs(method = "updateMouse",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
	public void injectUpdateMouse(final Args args)
	{
		final float f = RotationUtils.virtualPitch;
		final float f1 = RotationUtils.virtualYaw;
		RotationUtils.virtualYaw = (float)((double)RotationUtils.virtualYaw
			+ (double)args.get(0) * 0.15D);
		RotationUtils.virtualPitch = (float)((double)RotationUtils.virtualPitch
			+ (double)args.get(1) * 0.15D);
		RotationUtils.virtualPitch =
			MathHelper.clamp(RotationUtils.virtualPitch, -90.0F, 90.0F);
		RotationUtils.virtualPrevPitch += RotationUtils.virtualPitch - f;
		RotationUtils.virtualPrevYaw += RotationUtils.virtualYaw - f1;
		
		final EventRotation rotationEvent = new EventRotation(
			RotationUtils.virtualYaw, RotationUtils.virtualPitch);
		Client.onEvent(rotationEvent);
		
	}
}
