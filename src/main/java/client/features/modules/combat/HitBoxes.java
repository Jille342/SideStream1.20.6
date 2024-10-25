package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HitBoxes extends Module
{
	public HitBoxes()
	{
		super("HitBoxes", 0, Category.COMBAT);
	}
	
	public static NumberSetting size;
	public static ModeSetting mode;
	public static BooleanSetting ignoreTeams;
	
	@Override
	public void init()
	{
		super.init();
		ignoreTeams = new BooleanSetting("Ignore Teams", true);
		size = new NumberSetting("Size", 0.08, 0, 1, 0.01F);
		mode = new ModeSetting("Ignore Mode", "HurtTime",
			new String[]{"HurtTime"});
		
		addSetting(size, mode, ignoreTeams);
	}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			setTag(mode.getMode() + " " + size.getFlooredValue());
		}
	}
	
	public static float getSize(Entity entity)
	{
		if(entity instanceof LivingEntity)
		{
			if(mode.getMode().equalsIgnoreCase("HurtTime"))
			{
				if(entity instanceof PlayerEntity)
				{
					if(((LivingEntity)entity).hurtTime == 0)
					{
						if(ignoreTeams.isEnabled())
						{
							if(!ServerHelper.isTeammate((PlayerEntity)entity))
							{
								return (float)size.getValue();
							}else
							{
								return 0;
							}
						}else
						{
							if(((LivingEntity)entity).hurtTime == 0)
							{
								return (float)size.getValue();
							}
						}
					}
				}else
				{
					if(((LivingEntity)entity).hurtTime == 0)
					{
						return (float)size.getValue();
					}
				}
			}
		}
		return 0;
	}
	
}
