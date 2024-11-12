package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
import client.utils.MoveUtils;
import net.minecraft.network.packet.Packet;

public class Debug extends Module
{
	
	public Debug()
	{
		super("Debug", 0, Category.MISC);
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventMotion){
			ChatUtils.printChat(""+MoveUtils.getBaseMoveSpeed());
		}
		super.onEvent(e);
	}
	
}
