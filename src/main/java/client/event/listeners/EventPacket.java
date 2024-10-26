package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.packet.Packet;

@Setter
@Getter
public class EventPacket extends Event<EventPacket>
{
	
	Packet packet;
	
	public EventPacket(Packet packet)
	{
		this.packet = packet;
	}

}
