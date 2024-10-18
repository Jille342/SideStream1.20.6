/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.mixin.client;

import java.util.concurrent.ConcurrentLinkedQueue;

import client.Client;
import client.event.EventDirection;
import client.event.EventType;
import client.event.listeners.EventPacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
@Mixin(ClientConnection.class)
public abstract class MixinClientConnection
{

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void ChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.INCOMING);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }


    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void preSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.OUTGOING);
        e.setType(EventType.PRE);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"), cancellable = true)
    private void postSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        EventPacket e = new EventPacket(packet);
        e.setDirection(EventDirection.OUTGOING);
        e.setType(EventType.POST);
        Client.onEvent(e);

        if (e.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
