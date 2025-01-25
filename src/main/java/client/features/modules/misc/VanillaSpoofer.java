package client.features.modules.misc;

import client.event.listeners.EventPacket;
import client.features.modules.Module;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

public class VanillaSpoofer extends Module {
    public VanillaSpoofer() {
        super("Vanilla Spoofer",0, Category.MISC);
    }
    public void onPacket(EventPacket eventPacket){
        if(eventPacket.isOutgoing()){
            Packet<?> packet = eventPacket.getPacket();

            if (packet instanceof CustomPayloadC2SPacket && ((CustomPayloadC2SPacket) packet).payload() instanceof BrandCustomPayload) {
                eventPacket.cancel();
                mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new BrandCustomPayload("vanilla")));}
        }
    }
}
