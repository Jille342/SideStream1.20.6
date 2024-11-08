package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.NumberSetting;
import client.utils.MCTimerUtil;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Objects;

public class DebugSpeed extends Module
{
	
	NumberSetting timerSpeed;
	
	public DebugSpeed()
	{
		super("DebugSpeed", 0, Category.MOVEMENT);
	}
	
	@Override
	public void init()
	{
		super.init();
		timerSpeed = new NumberSetting("Timer Speed", 1, 1, 5, 0.01F);
		
		addSetting(timerSpeed);
	}
	
	public void onEnabled()
	{
		//MCTimerUtil.setTimerSpeed((float)this.timerSpeed.getValue());
		super.onEnabled();
	}
	
	public void onDisabled()
	{
		MCTimerUtil.setTimerSpeed(1);
		super.onDisabled();
	}
	
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventUpdate)
		{
			setTag("" + timerSpeed.getFlooredValue());
			if(Objects.requireNonNull(mc.interactionManager).isBreakingBlock())
			{
				MCTimerUtil.setTimerSpeed(1f);
				return;
			}else
			{
				MCTimerUtil.setTimerSpeed((float)this.timerSpeed.getValue());
			}
			if(Objects.requireNonNull(mc.player).isTouchingWater())
			{
				mc.player.setSprinting(true);
				mc.options.sprintKey.setPressed(true);
				mc.player.setSwimming(true);
				return;
			}else
			{
				mc.player.setSprinting(false);
				mc.options.sprintKey.setPressed(false);
				mc.player.setSwimming(false);
			}
			if(mc.player.isSneaking() || mc.player.isUsingItem())
				return;
			if(mc.player.isSprinting())
			{
				mc.player.setVelocity(mc.player.getVelocity().x * 0.6,
					mc.player.getVelocity().y, mc.player.getVelocity().z * 0.6);
			}else
			{
				float scala = 0.7f;
				if(mc.player.getStatusEffects().stream().toList().stream()
					.anyMatch(p -> p.getEffectType() == StatusEffects.SPEED))
				{
					final int amp = mc.player.getStatusEffects().stream()
						.toList().stream()
						.filter(p -> p.getEffectType() == StatusEffects.SPEED)
						.findAny().get().getAmplifier();
                    if (amp == 2) {
                        scala = 0.5f;
                    }
				}
				mc.player.setVelocity(mc.player.getVelocity().x * scala,
					mc.player.getVelocity().y,
					mc.player.getVelocity().z * scala);
			}
		}
		
	}
}
