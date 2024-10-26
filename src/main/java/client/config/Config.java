package client.config;

import client.Client;
/*    */ import java.io.File;
/*    */ import java.io.IOException;

public abstract class Config
{
	private final File file;
	private final String name;
	
	public Config(String name)
	{
		
		this.name = name;
		
		this.file = new File(Client.FOLDER, String.valueOf(name) + ".json");
		
		if(!this.file.exists())
		{
			
			try
			{
				
				file.createNewFile();
				
			}catch(IOException e)
			{
				throw new RuntimeException(e);
			}
			
			save();
		}
		
	}
	
	public final File getFile()
	{
		
		return this.file;
		
	}
	
	public final String getName()
	{
		
		return this.name;
		
	}
	
	public abstract void load();
	
	public abstract void save();
}
