package client.mixin.client;

import client.Client;
import client.event.listeners.EventInput;
import client.utils.MCUtil;
import client.utils.RotationUtils;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin implements MCUtil
{
	
	@Unique
	private void fixStrafe(final EventInput event)
	{
		final float diff =
			(RotationUtils.virtualYaw
				- Objects.requireNonNull(mc.player).getYaw()),
			f = (float)Math.sin(diff * ((float)Math.PI / 180F)),
			f1 = (float)Math.cos(diff * ((float)Math.PI / 180F));
		float multiplier = 1f;
		if(mc.player.isSneaking() || mc.player.isUsingItem())
			multiplier = 10;
		float forward =
			(float)(Math.round((event.getInput().movementForward * (double)f1
				+ event.getInput().movementSideways * (double)f) * multiplier))
				/ multiplier;
		float strafe =
			(float)(Math.round((event.getInput().movementSideways * (double)f1
				- event.getInput().movementForward * (double)f) * multiplier))
				/ multiplier;
		event.getInput().movementForward = forward;
		event.getInput().movementSideways = strafe;
	}
	
	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void injectTick(boolean slowDown, float slowDownFactor,
		CallbackInfo ci)
	{
		final KeyboardInput casted = (KeyboardInput)(Object)this;
		final EventInput event = new EventInput(casted, slowDownFactor);
		Client.onEvent(event);
		if(event.moveFix)
		{
			fixStrafe(event);
		}
		if(slowDownFactor == 0.2f)
		{
			if(Math.abs(event.getInput().movementForward) > slowDownFactor)
			{
				event.getInput().movementForward *= slowDownFactor;
			}
			if(Math.abs(event.getInput().movementSideways) > slowDownFactor)
			{
				event.getInput().movementSideways *= slowDownFactor;
			}
		}
	}
}
