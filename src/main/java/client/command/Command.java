package client.command;

import client.Client;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command
{
	
	protected MinecraftClient mc = Client.mc;
	
	public String name, description, syntax;
	public List<String> aliases = new ArrayList<String>();
	
	public Command(String name, String description, String syntax,
		String... aliases)
	{
		this.name = name;
		this.description = description;
		this.syntax = syntax;
		this.aliases = Arrays.asList(aliases);
	}
	
	public abstract boolean onCommand(String[] args, String command);
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getSyntax()
	{
		return syntax;
	}
	
	public void setSyntax(String syntax)
	{
		this.syntax = syntax;
	}
	
	public List<String> getAliases()
	{
		return aliases;
	}
	
	public void setAliases(List<String> aliases)
	{
		this.aliases = aliases;
	}
}
