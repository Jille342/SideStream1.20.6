package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

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
			EventPacket event = ((EventPacket)e);
			if(event.isIncoming())
			{
				Packet<?> p = event.getPacket();
				
				if(p instanceof PlaySoundS2CPacket)
				{
					if(((PlaySoundS2CPacket)p).getSound().toString()
						.toLowerCase().contains("attack"))
					{
						ChatUtils.printChat(
							((PlaySoundS2CPacket)p).getSound().toString());
					}
				}
			}
		}
		super.onEvent(e);
	}
	
}
