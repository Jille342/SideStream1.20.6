package client.settings;

import java.util.function.Supplier;

public class BooleanSetting extends Setting
{
	
	public boolean enabled;
	
	public BooleanSetting(String name, Supplier<Boolean> visibility,
		boolean enable)
	{
		super(name, visibility, enable);
		this.name = name;
		this.enabled = enable;
	}
	
	public BooleanSetting(String name, boolean enabled)
	{
		super(name, null, enabled);
		this.name = name;
		this.enabled = enabled;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enable)
	{
		this.enabled = enable;
	}
	public boolean getValue(){
		return enabled;
	}
	
	public void toggle()
	{
		enabled = !enabled;
	}
}
