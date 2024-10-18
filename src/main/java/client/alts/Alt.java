package client.alts;

import client.ui.gui.altmanager.LoginException;
import client.ui.gui.altmanager.LoginManager;
import client.ui.gui.altmanager.MicrosoftLoginManager;

public class Alt
{
	private String mask;
	
	private final String username;
	
	private final String password;
	
	public Alt(String username, String password)
	{
		this(username, password, "");
	}
	
	public Alt(String username, String password, String mask)
	{
		this.mask = "";
		this.username = username;
		this.password = password;
		this.mask = mask;
	}
	
	public String getMask()
	{
		return this.mask;
	}
	
	public void login()
	{
		
		if(getPassword() != null)
		{
			try
			{
				// TheAlteningAuthentication.mojang();
				MicrosoftLoginManager.login(getUsername(), getPassword());
			}catch(LoginException e)
			{
				throw new RuntimeException(e);
			}
		}else if(getUsername().contains("@alt.com"))
		{
			// TheAlteningAuthentication.theAltening();
			try
			{
				MicrosoftLoginManager.login(getUsername(), "password");
			}catch(LoginException e)
			{
				throw new RuntimeException(e);
			}
		}else
		{
			LoginManager.changeCrackedName(getUsername());
		}
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public void setMask(String mask)
	{
		this.mask = mask;
	}
}
