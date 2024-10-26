package client.alts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AltManager
{
	public List<Alt> alts = new ArrayList<>();
	
	@Setter
	private Alt lastAlt;

	public void login(Alt alt)
	{
		
		alt.login();
		
	}
	
	public void remove(int index)
	{
		alts.remove(index);
		
	}
}
