package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class Debug extends Module
{
	
	public Debug()
	{
		super("Debug", 0, Category.MISC);
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventPacket)
		{
				if(e.isIncoming()){
					EventPacket event = ((EventPacket)e);
					Packet<?> p = event.getPacket();
					if(p instanceof GameMessageS2CPacket){
						GameMessageS2CPacket gameMessageS2CPacket = (GameMessageS2CPacket) p;
						System.out.println(gameMessageS2CPacket.content().getString());					}
				}

		}
		super.onEvent(e);
	}
	
}
