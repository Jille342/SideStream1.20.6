package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.ModeSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;

public class InventoryMove extends Module
{
	boolean isClicked = false;
	ModeSetting mode;
	
	public InventoryMove()
	{
		super("InventoryMove", 0, Category.MOVEMENT);
	}
	
	@Override
	public void init()
	{
		super.init();
		mode = new ModeSetting("Mode", "Normal", "Stop", "Normal", "Ghost");
		addSetting(mode);
	}
	
	public void onEvent(Event<?> event)
	{
		
		if(event instanceof EventUpdate)
		{
			setTag(mode.getMode());
			if(mode.getMode().equalsIgnoreCase("Normal"))
			{
				isClicked = false;
			}
			if(mode.getMode().equalsIgnoreCase("Ghost"))
			{
				if(mc.currentScreen instanceof GenericContainerScreen)
				{
					isClicked = true;
				}
			}
			if(mc.currentScreen == null
				|| mc.currentScreen instanceof ChatScreen)
				return;
			if(!isClicked)
			{
				mc.options.forwardKey.setPressed(
					InputUtil.isKeyPressed(mc.getWindow().getHandle(),
						InputUtil.fromTranslationKey(
							mc.options.forwardKey.getBoundKeyTranslationKey())
							.getCode()));
				mc.options.leftKey
					.setPressed(
						InputUtil.isKeyPressed(mc.getWindow().getHandle(),
							InputUtil.fromTranslationKey(
								mc.options.leftKey.getBoundKeyTranslationKey())
								.getCode()));
				mc.options.rightKey
					.setPressed(
						InputUtil.isKeyPressed(mc.getWindow().getHandle(),
							InputUtil.fromTranslationKey(
								mc.options.rightKey.getBoundKeyTranslationKey())
								.getCode()));
				mc.options.backKey
					.setPressed(
						InputUtil.isKeyPressed(mc.getWindow().getHandle(),
							InputUtil.fromTranslationKey(
								mc.options.backKey.getBoundKeyTranslationKey())
								.getCode()));
				mc.options.jumpKey
					.setPressed(
						InputUtil.isKeyPressed(mc.getWindow().getHandle(),
							InputUtil.fromTranslationKey(
								mc.options.jumpKey.getBoundKeyTranslationKey())
								.getCode()));
				mc.options.sprintKey.setPressed(
					InputUtil.isKeyPressed(mc.getWindow().getHandle(),
						InputUtil.fromTranslationKey(
							mc.options.sprintKey.getBoundKeyTranslationKey())
							.getCode()));
			}else
			{
				mc.options.forwardKey.setPressed(false);
				mc.options.leftKey.setPressed(false);
				mc.options.rightKey.setPressed(false);
				mc.options.backKey.setPressed(false);
				mc.options.jumpKey.setPressed(false);
				mc.options.sprintKey.setPressed(false);
			}
		}
		if(event instanceof EventPacket)
		{
			EventPacket ep = (EventPacket)event;
			
			if(!this.mode.getMode().equals("Normal"))
			{
				Packet<?> packet = ep.getPacket();
				if(event.isOutgoing())
				{
					if(packet instanceof CloseHandledScreenC2SPacket)
					{
						CloseHandledScreenC2SPacket closeScreenS2CPacket =
							(CloseHandledScreenC2SPacket)ep.getPacket();
						if(this.isClicked)
						{
							ep.setCancelled(true);
							this.isClicked = false;
						}
					}
					
					if(packet instanceof OpenScreenS2CPacket)
					{
						ep.setCancelled(true);
					}
					
					if(packet instanceof ClickSlotC2SPacket)
					{
						ClickSlotC2SPacket clickWindow =
							(ClickSlotC2SPacket)ep.getPacket();
						if(mc.currentScreen instanceof InventoryScreen)
						{
							if((clickWindow.getButton() == 4
								|| clickWindow.getButton() == 3)
								&& clickWindow.getButton() == -999)
							{
								ep.setCancelled(true);
							}else
							{
								// mc.player.networkHandler.sendPacket(new
								// ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.));
								this.isClicked = true;
							}
						}
					}
					
				}
			}
		}
	}
}
