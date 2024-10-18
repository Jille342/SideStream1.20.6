/*    */
package client.config;

/*    */
/*    */ import client.config.configs.AltConfig;
/*    */ import client.config.configs.ModuleConfig;
/*    */
/*    */ import client.config.configs.SettingConfig;
import client.utils.Logger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class ConfigManager
/*    */ {
	/*    */ public List<Config> contents;
	
	/*    */
	/*    */ public ConfigManager()
	{
		/* 20 */ Logger.logConsole("loading files...");
		/* 21 */ this.contents = new ArrayList<>();
		/*    */
		/* 23 */ add(AltConfig.class);
		/* 24 */ add(ModuleConfig.class);
		add(SettingConfig.class);
		/* 25 */
		/*    */
		/* 27 */ for(Config config : getConfigs())
		{
			/* 28 */ config.load();
			/*    */ }
		/* 30 */ Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			/*    */ for(Config config : getConfigs())
			{
				/*    */ config.save();
				/*    */ }
			/*    */ }));
		/*    */ }
		
	/*    */
	/*    */ public Config getFile(String name)
	{
		/* 38 */ if(this.contents == null)
		{
			/* 39 */ return null;
			/*    */ }
		/* 41 */ Iterator<Config> var3 = this.contents.iterator();
		/* 42 */ while(var3.hasNext())
		{
			/* 43 */ Config file = var3.next();
			/* 44 */ if(file.getName().equalsIgnoreCase(name))
			{
				/* 45 */ return file;
				/*    */ }
			/*    */ }
		/* 48 */ return null;
		/*    */ }
		
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
		
	/*    */
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
	/*    */ }
	
/*
 * Location: C:\Users\Null\Downloads\Qurobito
 * LEAK\Qurobito.jar!\client\config\ConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version: 1.1.3
 */
