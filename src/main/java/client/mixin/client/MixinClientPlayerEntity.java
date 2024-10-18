package client.mixin.client;

import client.Client;
import client.event.EventType;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.utils.TickManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity {
    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
            ordinal = 0), method = "tick()V")
    private void onTick(CallbackInfo ci)
    {
        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setType(EventType.PRE);
        Client.onEvent(eventUpdate);
    }
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    float coffee_replaceMovementPacketYaw(ClientPlayerEntity instance) {
        EventMotion event = new EventMotion(instance.getX(), instance.getY(), instance.getZ(),instance.getYaw(),instance.getPitch(),instance.isOnGround());
        Client.onEvent(event);
        return event.getYaw();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    float coffee_replaceMovementPacketPitch(ClientPlayerEntity instance) {
        EventMotion event = new EventMotion(instance.getX(), instance.getY(), instance.getZ(),instance.getYaw(),instance.getPitch(),instance.isOnGround());
        Client.onEvent(event);
        return event.getPitch();
    }

}
