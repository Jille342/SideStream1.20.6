package client.alts;

import client.Client;
import client.utils.AlteningUtils;
import client.utils.UUIDUtils;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.session.Session;
import net.minecraft.util.Uuids;

import java.util.Objects;
import java.util.Optional;

@Getter
public class Alt
{
	@Setter
	private String mask;
	
	private final String username;
	
	private final String password;
	MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
	MicrosoftAuthResult result;
	
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
		
		if(!getPassword().isEmpty())
		{
			try
			{
				// TheAlteningAuthentication.mojang();
				result =authenticator.loginWithCredentials(getUsername(),getPassword());
				Session session = new Session(result.getProfile().getName(), UUIDUtils.uuidFromString(result.getProfile().getId().toString()) ,
						result.getAccessToken(), Optional.empty(), Optional.empty(),
						Session.AccountType.MOJANG);
				Client.IMC.setSession(session);
			} catch (MicrosoftAuthenticationException | RuntimeException e) {
                throw new RuntimeException(e);
            }
        }else if(getUsername().contains("@alt.com"))
		{
			AlteningUtils.login(getUsername());
		}else
		{
			Session session =
					new Session(getUsername(), Uuids.getOfflinePlayerUuid(getUsername()), "",
							Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);

			Client.IMC.setSession(session);
		}
	}
	
}
