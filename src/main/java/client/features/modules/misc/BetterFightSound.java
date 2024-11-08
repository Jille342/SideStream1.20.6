package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;

public class BetterFightSound extends Module
{
	
	public BetterFightSound()
	{
		super("BetterFightSound", 0, Category.MISC);
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventPacket event)
		{
			if(event.isIncoming())
			{
				Packet<?> p = event.getPacket();
				
				if(p instanceof PlaySoundS2CPacket)
				{
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_STRONG)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p)
						.getSound() == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK)
						event.setCancelled(true);
					if(((PlaySoundS2CPacket)p).getSound().toString()
						.toLowerCase().contains("entity.player.attack"))
					{
						event.setCancelled(true);
					}
				}
			}
		}
		super.onEvent(e);
	}
	
}
