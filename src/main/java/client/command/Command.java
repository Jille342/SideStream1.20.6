package client.command;

import client.Client;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command
{

	protected MinecraftClient mc = Client.mc;

	@Setter
	@Getter
	public String name, description, syntax;
	@Setter
	@Getter
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
}
