
package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.mixin.client.IEntityVelocityUpdateS2CPacketMixin;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class AntiVelocity extends Module
{
	ModeSetting mode;
	NumberSetting vertical;
	NumberSetting horizontal;
	NumberSetting chance;
	BooleanSetting clickOnly;
	BooleanSetting targetCheck;
	public static long lastVelocity = System.currentTimeMillis();


	public AntiVelocity()
	{
		super("AntiVelocity", 0, Category.COMBAT);
	}
	
	public void init()
	{
		super.init();
		this.mode =
			new ModeSetting("Mode", "Simple", new String[]{"Simple", "Legit"});
		this.vertical = new NumberSetting("Vertical", 100, 0.0, 100.0, 1.0);
		this.horizontal =
			new NumberSetting("Horizontal", 90.0, 0.0, 100.0, 1.0);
		this.chance = new NumberSetting("Chance", 90.0, 0.0, 100.0, 1.0);
		this.clickOnly = new BooleanSetting("Click Only", false);
		targetCheck = new BooleanSetting( "Target Check",true);
		this.addSetting(this.mode, this.vertical, this.horizontal, this.chance,
			this.clickOnly,targetCheck);
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			setTag("H:"+horizontal.getValue()+ " V:"+vertical.getValue());
		}
		if(e instanceof EventPacket event)
		{
			if(event.getPacket() instanceof EntityDamageS2CPacket){
				EntityDamageS2CPacket p = (EntityDamageS2CPacket) event.getPacket();
				if (p.entityId() == mc.player.getId() && p.sourceCauseId() != 8) {
					lastVelocity = System.currentTimeMillis();
				}

			}
			if(this.mode.getMode().equalsIgnoreCase("Simple"))
			{
				if(targetCheck.isEnabled() && (LegitAura2.target != null || AimAssist.target !=null)) {
					if (this.clickOnly.isEnabled() && !mc.options.useKey.isPressed()) {
						return;
					}
					if (this.chance.getValue() != 100.0) {
						final double ch = Math.random();
						if (ch >= this.chance.getValue() / 100.0) {
							return;
						}
					}

					if (event
							.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
						assert mc.player != null;
						if (packet.getId() == mc.player.getId()) {

							double velX = ((double) packet.getVelocityX() / 8000); // don't
							// ask
							// me
							// why
							// they
							// did
							// this
							double velY = ((double) packet.getVelocityY() / 8000);
							double velZ = ((double) packet.getVelocityZ() / 8000);
							velX *= horizontal.getValue() / 100;
							velY *= vertical.getValue() / 100;
							velZ *= horizontal.getValue() / 100;
							IEntityVelocityUpdateS2CPacketMixin jesusFuckingChrist =
									(IEntityVelocityUpdateS2CPacketMixin) packet;
							jesusFuckingChrist.setVelocityX((int) (velX * 8000));
							jesusFuckingChrist.setVelocityY((int) (velY * 8000));
							jesusFuckingChrist.setVelocityZ((int) (velZ * 8000));

						}
					}
				}
			}
		}
	}
}
