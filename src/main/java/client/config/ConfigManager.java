/*    */
package client.config;

/*    */
/*    */ import client.Client;
import client.config.configs.AltConfig;
/*    */
import client.config.configs.ModuleConfig;
/*    */
/*    */ import client.config.configs.SettingsConfig;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.settings.*;
import client.utils.Logger;
import org.apache.commons.io.FilenameUtils;
/*    */ import java.io.*;
import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;

import static client.config.configs.SettingsConfig.getSettingbyName;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class ConfigManager
/*    */ {
	/*    */ public List<Config> contents;
	private List<File> customConfigs = new ArrayList<>();
	
	public static final File CONFIGS_DIR =
		new File(Client.FOLDER, "CustomConfigs");
	
	public ConfigManager()
	{
		setCustomConfigs(loadConfigs());
		Logger.logConsole("loading files...");
		this.contents = new ArrayList<>();
		add(AltConfig.class);
		add(ModuleConfig.class);
		add(SettingsConfig.class);
		
		for(Config config : getConfigs())
		{
			config.load();
		}
	 Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		 for(Config config : getConfigs())
			{
			 config.save();
			 }
		 }));
	}
		

	public Config getFile(String name)
	{
	if(this.contents == null)
		{
		 return null;
		 }
		 Iterator<Config> var3 = this.contents.iterator();
 while(var3.hasNext())
		{
		Config file = var3.next();
		 if(file.getName().equalsIgnoreCase(name))
			{
			return file;
		 }
		}
	return null;
	 }
		
	/*    */
	/*    */ public Config getFile(Class<? extends Config> theFile)
	{
		/* 52 */ if(this.contents == null)
		{
			/* 53 */ return null;
			/*    */ }
		/* 55 */ for(Config file : this.contents)
		{
			/* 56 */ if(file.getClass() == theFile)
			{
				/* 57 */ return file;
				/*    */ }
			/*    */ }
		/* 60 */ return null;
		/*    */ }
		
	/*    */ public void add(Class<? extends Config> content)
	{
		/*    */ try
		{
			/* 65 */ this.contents.add(content.newInstance());
			/* 66 */ }catch(InstantiationException e)
		{
			/* 67 */ e.printStackTrace();
			/* 68 */ }catch(IllegalAccessException e)
		{
			/*    */
			/* 70 */ e.printStackTrace();
			/*    */ }
		/*    */ }
		
	/*    */
	/*    */ public List<Config> getConfigs()
	{
		/* 75 */ return this.contents;
		/*    */ }
		
	public boolean loadConfig(final String configName)
	{
		if(configName == null)
			return false;
		final File config = this.findConfig(configName);
		if(config == null || !config.exists())
			return false;
		this.load(config);
		return true;
	}
	
	public void init()
	{
		setCustomConfigs(loadConfigs());
	}
	
	public void setCustomConfigs(final ArrayList<File> contents)
	{
		this.customConfigs = contents;
	}
	
	public List<File> getCustomConfigs()
	{
		return customConfigs;
	}
	
	public boolean saveConfig(final String configName)
	{
		if(configName == null)
			return false;
		File newConfig = null;
		if((newConfig = this.findConfig(configName)) == null)
		{
			newConfig = createConfig(configName);
		}
		this.save(newConfig);
		return true;
	}
	
	public boolean deleteConfig(final String configName)
	{
		if(configName == null)
			return false;
		final File config;
		if((config = this.findConfig(configName)) != null)
		{
			return config.exists() && config.delete();
		}
		return false;
	}
	
	private static ArrayList<File> loadConfigs()
	{
		final ArrayList<File> loadedConfigs = new ArrayList<>();
		if(!CONFIGS_DIR.exists())
		{
			CONFIGS_DIR.mkdir();
		}
		final File[] files = CONFIGS_DIR.listFiles();
		if(files != null)
		{
			for(final File file : files)
			{
				if(FilenameUtils.getExtension(file.getName()).equals("json"))
					loadedConfigs.add(new File(
						FilenameUtils.removeExtension(file.getName())));
			}
		}
		return loadedConfigs;
	}
	
	public File findConfig(final String configName)
	{
		if(configName == null)
			return null;
		for(final File config : this.getCustomConfigs())
		{
			if(config.getName().equalsIgnoreCase(configName))
				return config;
		}
		File file2 = new File(CONFIGS_DIR, configName + ".json");
		
		if(file2.exists())
			return file2;
		
		return null;
	}
	
	public File createConfig(String name)
	{
		File file = new File(CONFIGS_DIR, name + ".json");
		
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}catch(IOException ignored)
			{}
		}
		return file;
	}
	
	public void load(File file)
	{
		try
		{
			BufferedReader var7 = new BufferedReader(new FileReader(file));
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
					client.features.modules.Module module =
						ModuleManager.getModulebyLowerName(arguments[0]);
					if(module != null)
					{
						Setting<?> setting =
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
	
	public void save(File file)
	{
		try
		{
			BufferedWriter var4 = new BufferedWriter(new FileWriter(file));
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
