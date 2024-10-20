package client.command.impl;

import client.command.Command;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.utils.ChatUtils;

public class Toggle extends Command
{
	
	public Toggle()
	{
		super("Toggle", "", "toggle <Module>", "toggle", "t");
	}
	
	@Override
	public boolean onCommand(String[] args, String command)
	{
		if(args.length == 1)
		{
			for(Module m : ModuleManager.modules)
			{
				if(m.getName().equalsIgnoreCase(args[0]))
				{
					m.toggle();
					ChatUtils.printChat("Toggled:" + m.getName());
					return true;
				}
			}
		}
		return false;
	}
}
