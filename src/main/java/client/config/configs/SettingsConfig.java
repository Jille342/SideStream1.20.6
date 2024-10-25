package client.config.configs;

import client.config.Config;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.settings.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsConfig extends Config
{
	public SettingsConfig()
	{
		super("settingsconfig");
	}
	
	public static Setting getSettingbyName(Module module, String str)
	{
		return module.settings.stream()
			.filter(m -> m.name.equalsIgnoreCase(str)).findFirst().orElse(null);
	}
	
	public void load()
	{
		try
		{
			BufferedReader var7 = new BufferedReader(new FileReader(getFile()));
			while(true)
			{
				String line;
				if((line = var7.readLine()) == null)
				{
					var7.close();
					return;
				}
				String[] arguments = line.split(":");
				if(arguments.length == 3)
				{
					Module module =
						ModuleManager.getModulebyLowerName(arguments[0]);
					if(module != null)
					{
						Setting setting =
							getSettingbyName(module, arguments[1]);
						if(setting != null)
						{
							if(setting instanceof NumberSetting)
							{
								((NumberSetting)setting)
									.setValue(Double.parseDouble(arguments[2]));
							}
							if(setting instanceof BooleanSetting)
							{
								((BooleanSetting)setting).setEnabled(
									Boolean.parseBoolean(arguments[2]));
							}
							if(setting instanceof ModeSetting)
							{
								((ModeSetting)setting).setModes(arguments[2]);
							}
							
							if(setting instanceof KeyBindSetting)
							{
								((KeyBindSetting)setting)
									.setKeyCode(Integer.parseInt(arguments[2]));
							}
							
						}
					}
				}
			}
		}catch(FileNotFoundException var6)
		{
			var6.printStackTrace();
		}catch(IOException var71)
		{
			var71.printStackTrace();
		}
	}
	
	public void save()
	{
		try
		{
			BufferedWriter var4 = new BufferedWriter(new FileWriter(getFile()));
			for(Module module : ModuleManager.modules)
			{
				for(Setting setting : module.settings)
				{
					String text = "";
					if(setting instanceof NumberSetting)
					{
						text = String.valueOf(module.getName().toLowerCase()
							+ ":" + setting.name.toLowerCase() + ":"
							+ ((NumberSetting)setting).getValue());
					}
					if(setting instanceof KeyBindSetting)
					{
						text = String.valueOf(module.getName().toLowerCase()
							+ ":" + setting.name.toLowerCase() + ":"
							+ ((KeyBindSetting)setting).getKeyCode());
					}
					
					if(setting instanceof ModeSetting)
					{
						text = String.valueOf(module.getName().toLowerCase()
							+ ":" + setting.name.toLowerCase() + ":"
							+ ((ModeSetting)setting).getMode());
					}
					if(setting instanceof BooleanSetting)
					{
						text = String.valueOf(module.getName().toLowerCase()
							+ ":" + setting.name.toLowerCase() + ":"
							+ ((BooleanSetting)setting).isEnabled());
					}
					
					var4.write(text);
					var4.newLine();
				}
			}
			var4.close();
		}catch(IOException var41)
		{
			var41.printStackTrace();
		}
	}
}
