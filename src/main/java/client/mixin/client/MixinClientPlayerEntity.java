package client.mixin.client;

import client.Client;
import client.event.EventType;
import client.event.listeners.EventMotion;
import client.event.listeners.EventMove;
import client.event.listeners.EventUpdate;
import client.event.listeners.EventUpdateVelocity;
import client.utils.RotationUtils;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity
{
	@Inject(
			method = {"sendMovementPackets"},
			at = {@At("HEAD")},
			cancellable = true
	)
	public void injectSendMovementPacketsPre(CallbackInfo ci) {
		EventUpdate event = new EventUpdate();
		Client.onEvent(event);
		if (event.isCancelled()) {
			ci.cancel();
		}

	}

	
	@Redirect(method = "sendMovementPackets",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
	float coffee_replaceMovementPacketYaw(ClientPlayerEntity instance)
	{
		EventMotion event =
			new EventMotion(instance.getX(), instance.getY(), instance.getZ(),
				instance.getYaw(), instance.getPitch(), instance.isOnGround());
		Client.onEvent(event);
		RotationUtils.virtualYaw = event.getYaw();
		return event.getYaw();
	}
@ModifyArgs(
			method = {"move"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
			)
	)
	public void injectMove(Args args) {
		Vec3d movement = (Vec3d)args.get(1);
		EventMove event = new EventMove(movement.getX(), movement.getY(), movement.getZ());
	Client.onEvent(event);
		args.set(1, new Vec3d(event.getX(), event.getY(), event.getZ()));
	}



	@Redirect(method = "sendMovementPackets",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
	float coffee_replaceMovementPacketPitch(ClientPlayerEntity instance)
	{
		EventMotion event =
			new EventMotion(instance.getX(), instance.getY(), instance.getZ(),
				instance.getYaw(), instance.getPitch(), instance.isOnGround());
		Client.onEvent(event);
		RotationUtils.virtualPitch = event.getPitch();
		return event.getPitch();
	}
	@ModifyArgs(
			method = {"tickMovement"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V"
			)
	)
	private void injected(Args args) {
		EventUpdateVelocity event = new EventUpdateVelocity(1.0F, MinecraftClient.getInstance().player.getYaw());
		Client.onEvent(event);
		if (event.yaw != MinecraftClient.getInstance().player.getYaw()) {
			args.set(0, true);
		}

	}


}
