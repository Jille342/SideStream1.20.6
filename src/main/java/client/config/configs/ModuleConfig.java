/*    */
package client.config.configs;
/*    */

/*    */

/*    */ import client.config.Config;
/*    */ import client.features.modules.Module;
import client.features.modules.ModuleManager;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileReader;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;

/*    */
/*    */
/*    */ public class ModuleConfig/*    */ extends Config
/*    */ {
	/*    */ public ModuleConfig()
	{
		/* 20 */ super("modules");
		/*    */ }
		
	/*    */
	/*    */ public void load()
	{
		/*    */ try
		{
			/* 25 */ BufferedReader var6 =
				new BufferedReader(new FileReader(getFile()));
			/*    */ String line;
			/* 27 */ while((line = var6.readLine()) != null)
			{
				/* 28 */ String[] arguments = line.split(":");
				/* 29 */ if(arguments.length == 3)
				{
					/* 30 */ Module mod =
						ModuleManager.getModulebyLowerName(arguments[0]);
					/* 31 */ if(mod != null)
					{
						/* 32 */ mod
							.setEnabled(Boolean.parseBoolean(arguments[1]));
						/* 33 */ mod.setKeyCode(Integer.parseInt(arguments[2]));
						/*    */ }
					/*    */ }
				/*    */ }
			/* 37 */ var6.close();
			/* 38 */ }catch(FileNotFoundException var5)
		{
			/* 39 */ var5.printStackTrace();
			/* 40 */ }catch(IOException var61)
		{
			/* 41 */ var61.printStackTrace();
			/*    */ }
		/*    */ }
		
	/*    */
	/*    */ public void save()
	{
		/*    */ try
		{
			/* 47 */ BufferedWriter var4 =
				new BufferedWriter(new FileWriter(getFile()));
			/* 48 */ Iterator<Module> var3 = ModuleManager.modules.iterator();
			/* 49 */ while(var3.hasNext())
			{
				/* 50 */ Module mod = var3.next();
				/* 51 */ String text =
					String.valueOf(mod.getName().toLowerCase()) + ":"
						+ mod.isEnabled() + ":" + mod.getKeyCode();
				/* 52 */ var4.write(text);
				/* 53 */ var4.newLine();
				/*    */ }
			/* 55 */ var4.close();
			/* 56 */ }catch(IOException var41)
		{
			/* 57 */ var41.printStackTrace();
			/*    */ }
		/*    */ }
	/*    */ }
	
/*
 * Location: C:\Users\Null\Downloads\Qurobito
 * LEAK\Qurobito.jar!\client\config\configs\ModuleConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version: 1.1.3
 */
