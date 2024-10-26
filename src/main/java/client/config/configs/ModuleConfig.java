
package client.config.configs;

import client.config.Config;
/*    */ import client.features.modules.Module;
import client.features.modules.ModuleManager;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ /*    */ import java.io.FileReader;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */

public class ModuleConfig extends Config
{
	public ModuleConfig()
	{
		/* 20 */ super("modules");
		/*    */ }
		
	public void load()
	{
		try
		{
			BufferedReader var6 = new BufferedReader(new FileReader(getFile()));
			String line;
			while((line = var6.readLine()) != null)
			{
				String[] arguments = line.split(":");
				if(arguments.length == 3)
				{
					Module mod =
						ModuleManager.getModulebyLowerName(arguments[0]);
					if(mod != null)
					{
						mod.setEnabled(Boolean.parseBoolean(arguments[1]));
						mod.setKeyCode(Integer.parseInt(arguments[2]));
					}
				}
			}
			var6.close();
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void save()
	{
		try
		{
			BufferedWriter var4 = new BufferedWriter(new FileWriter(getFile()));
			
			for(Module mod : ModuleManager.modules)
			{
				
				String text = String.valueOf(mod.getName().toLowerCase()) + ":"
					+ mod.isEnabled() + ":" + mod.getKeyCode();
				var4.write(text);
				var4.newLine();
			}
			var4.close();
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
