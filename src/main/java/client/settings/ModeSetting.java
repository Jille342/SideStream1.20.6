package client.settings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting
{
	
	public int index;
	public List<String> modes;
	public boolean expand;
	
	public ModeSetting(String name, Supplier<Boolean> visibility,
		String defaultMode, String... modes)
	{
		super(name, visibility, defaultMode);
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}
	
	public ModeSetting(String name, String defaultMode, String... modes)
	{
		super(name, null, defaultMode);
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
	}
	
	public String getMode()
	{
		return modes.get(index);
	}
	public String getValue(){
		return modes.get(index);
	}
	
	public void setModes(String a)
	{
		index = modes.indexOf(a);
	}
	
	public boolean is(String mode)
	{
		return index == modes.indexOf(mode);
	}
	
	public void cycle()
	{
		if(index < modes.size() - 1)
		{
			index++;
		}else
		{
			index = 0;
		}
	}
	
}
