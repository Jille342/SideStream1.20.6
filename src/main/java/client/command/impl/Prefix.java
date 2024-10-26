package client.command.impl;

import client.command.Command;
import client.command.CommandManager;

public class Prefix extends Command
{
	
	public Prefix()
	{
		super("Prefix", "", "prefix <String>", "prefix");
	}
	
	@Override
	public boolean onCommand(String[] args, String command)
	{
		if(args.length == 1)
		{
			CommandManager.prefix = args[0];
			return true;
		}
		return false;
	}
}
