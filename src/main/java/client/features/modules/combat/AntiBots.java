package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.settings.ModeSetting;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public final class AntiBots extends Module
{
	
	public AntiBots()
	{
		super("AntiBots", 0, Category.COMBAT);
	}
	
	static ModeSetting mode;
	
	@Override
	public void init()
	{
		super.init();
		mode = new ModeSetting("Mode ", "Shotbow", new String[]{"Hypixel",
			"Mineplex", "Shotbow", "ShotbowTeams", "MatrixFlying"});
		addSetting(mode);
	}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			setTag(mode.getMode());
			switch(mode.getMode())
			{
				case "Hypixel":
				
				break;
				case "Mineplex":
				break;
				case "Shotbow":
				
				break;
			}
		}
		
	}
	
	public static boolean isHypixelBot(PlayerEntity player)
	{
		final String valid =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
		final String name = player.getName().getString();
		
		for(int i = 0; i < name.length(); i++)
		{
			final String c = String.valueOf(name.charAt(i));
			if(!valid.contains(c))
			{
				return true;
			}
		}
		
		if(player.age < 20 && (int)player.getX() == (int)mc.player.getX()
			&& (int)player.getZ() == (int)mc.player.getZ()
			&& player.isInvisible())
			return true;
		return false;
	}
	
	private static boolean isNoArmor(final PlayerEntity entity)
	{
		for(int i = 0; i < 4; ++i)
		{
			
			if(entity.getInventory().getStack(i).getItem() != null)
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isBot(PlayerEntity e)
	{
		if(!(ModuleManager.getModulebyClass(AntiBots.class).isEnabled()))
			return false;
        return switch (mode.getMode()) {
            case "Shotbow" -> (e.getHealth() == 20
                    || Objects.requireNonNull(mc.getNetworkHandler())
                    .getPlayerListEntry(e.getUuid()) == null);
            case "Hypixel" -> isHypixelBot(e);
            case "ShotbowTeams" -> e.getTeamColorValue() == 0;
            default -> false;
        };
    }
	
}
