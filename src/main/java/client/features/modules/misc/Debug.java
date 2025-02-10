package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
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
		//ChatUtils.printChat(String.valueOf(mc.player.getVelocity().length()));
		super.onEvent(e);
	}
	
}
