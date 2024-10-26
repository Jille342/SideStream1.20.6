package client.alts;

import client.ui.gui.altmanager.LoginException;
import client.ui.gui.altmanager.LoginManager;
import client.ui.gui.altmanager.MicrosoftLoginManager;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Alt
{
	@Setter
	private String mask;
	
	private final String username;
	
	private final String password;
	
	public Alt(String username, String password)
	{
		this(username, password, "");
	}
	
	public Alt(String username, String password, String mask)
	{
		this.username = username;
		this.password = password;
		this.mask = mask;
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
	
}
