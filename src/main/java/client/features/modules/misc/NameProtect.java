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
		if(e instanceof EventPacket)
		{
			EventPacket event = ((EventPacket)e);
			if(event.isIncoming())
			{
				Packet<?> p = event.getPacket();
				
				if(p instanceof ChatMessageS2CPacket)
				{
					
					ChatMessageS2CPacket packet =
						(ChatMessageS2CPacket)event.getPacket();
					if(Objects.requireNonNull(packet.unsignedContent())
						.getString().contains(mc.player.getName().getString()))
					{
						String temp = packet.unsignedContent().getString();
						ChatUtils.printChatNoName(
							temp.replaceAll(String.valueOf(mc.player.getName()),
								"\247d" + Client.NAME + "User" + "\247r"));
						event.setCancelled(true);
					}else
					{
						String[] list = new String[]{"join", "left", "leave",
							"leaving", "lobby", "server", "fell", "died",
							"slain", "burn", "void", "disconnect", "kill", "by",
							"was", "quit", "blood", "game"};
						for(String str : list)
						{
							if(packet.unsignedContent().getString()
								.toLowerCase().contains(str))
							{
								event.setCancelled(true);
								break;
							}
						}
					}
				}
				
			}
		}
		super.onEvent(e);
	}
	
}
