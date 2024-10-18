package client.event.listeners;

import client.event.Event;
import net.minecraft.network.packet.Packet;

public class EventPacket extends Event<EventPacket> {
	
	Packet packet;

	public EventPacket(Packet packet) {
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}
}
