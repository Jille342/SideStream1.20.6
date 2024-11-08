package client.features.modules.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

import java.util.Objects;

public class NameProtect extends Module
{
	
	public NameProtect()
	{
		super("NameProtect", 0, Category.MISC);
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventPacket event)
		{
            if(event.isIncoming())
			{
				Packet<?> p = event.getPacket();
				if(p instanceof ChatMessageS2CPacket packet)
				{
					if(Objects.requireNonNull(Objects.requireNonNull(packet.unsignedContent())
                            .getLiteralString()).contains(Objects.requireNonNull(Objects.requireNonNull(mc.player).getName().getLiteralString())))
					{
						String temp = packet.unsignedContent().getLiteralString();
						ChatUtils.printChatNoName(
							temp.replaceAll(String.valueOf(mc.player.getName().getLiteralString()), "\247d" + Client.NAME + "User" + "\247r"));
						event.setCancelled(true);
					}
				}
				
			}
		}
		super.onEvent(e);
	}
	
}
