/*    */
package client.config.configs;

/*    */
/*    */ import client.Client;
/*    */ import client.alts.Alt;
/*    */ import client.config.Config;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Iterator;

/*    */
/*    */ public class AltConfig/*    */ extends Config
/*    */ {
	/*    */ public AltConfig()
	{
		/* 17 */ super("alts");
		/*    */ }
		
	/*    */
	/*    */
	/*    */ public void load()
	{
		/*    */ try
		{
			/* 23 */ BufferedReader var8 =
				new BufferedReader(new FileReader(getFile()));
			/*    */ String s;
			/* 25 */ while((s = var8.readLine()) != null)
			{
				/* 26 */ if(s.contains("\t"))
				{
					/* 27 */ s = s.replace("\t", "    ");
					/*    */ }
				/* 29 */ if(s.contains("    "))
				{
					/* 30 */ String[] arrayOfString1 = s.split("    ");
					/* 31 */ String[] var9 = arrayOfString1[1].split(":");
					/* 32 */ if(var9.length == 2)
					{
						/* 33 */ Client.altManager.getAlts()
							.add(new Alt(var9[0], var9[1]));
						/*    */ continue;
						/*    */ }
					/* 36 */ String var10 = var9[1];
					/* 37 */ for(int i1 = 2; i1 < var9.length; i1++)
					{
						/* 38 */ var10 = String.valueOf(var10) + ":" + var9[i1];
						/*    */ }
					/* 40 */ Client.altManager.getAlts()
						.add(new Alt(var9[0], var10));
					/*    */
					/*    */ continue;
					/*    */ }
				/* 44 */ String[] account = s.split(":");
				/* 45 */ if(account.length == 1)
				{
					/* 46 */ Client.altManager.getAlts()
						.add(new Alt(account[0], ""));
					continue;
					/*    */ }
				/* 48 */ if(account.length == 2)
				{
					/* 49 */ Client.altManager.getAlts()
						.add(new Alt(account[0], account[1]));
					/*    */ continue;
					/*    */ }
				/* 52 */ String pw = account[1];
				/* 53 */ for(int i = 2; i < account.length; i++)
				{
					/* 54 */ pw = String.valueOf(pw) + ":" + account[i];
					/*    */ }
				/* 56 */ Client.altManager.getAlts()
					.add(new Alt(account[0], pw));
				/*    */ }
			/*    */
			/*    */
			/* 60 */ var8.close();
			/*    */ }
		/* 62 */ catch(FileNotFoundException var7)
		{
			/* 63 */ var7.printStackTrace();
			/*    */ }
		/* 65 */ catch(IOException var81)
		{
			/* 66 */ var81.printStackTrace();
			/*    */ }
		/*    */ }
		
	/*    */
	/*    */
	/*    */ public void save()
	{
		/*    */ try
		{
			/* 73 */ PrintWriter var4 = new PrintWriter(getFile());
			/* 74 */ Iterator<Alt> var3 =
				Client.altManager.getAlts().iterator();
			/* 75 */ while(var3.hasNext())
			{
				/* 76 */ Alt alt = var3.next();
				/* 77 */ if(alt.getMask().equals(""))
				{
					/* 78 */ String str = String.valueOf(alt.getUsername())
						+ ":" + alt.getPassword();
					/* 79 */ var4.println(str);
					continue;
					/*    */ }
				/* 81 */ String text = String.valueOf(alt.getMask()) + "    "
					+ alt.getUsername() + ":" + alt.getPassword();
				/* 82 */ var4.println(text);
				/*    */ }
			/*    */
			/* 85 */ var4.close();
			/*    */ }
		/* 87 */ catch(FileNotFoundException var41)
		{
			/* 88 */ var41.printStackTrace();
			/*    */ }
		/*    */ }
	/*    */ }
