package client.features.modules.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

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
				
				if(p instanceof ChatMessageC2SPacket)
				{
					
					ChatMessageC2SPacket packet =
						(ChatMessageC2SPacket)event.getPacket();
					if(packet.chatMessage()
						.contains(mc.player.getName().getString()))
					{
						String temp = packet.chatMessage();
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
							if(packet.chatMessage().toLowerCase().contains(str))
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
