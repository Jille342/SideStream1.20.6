package client.settings;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
@Getter
public class KeyBindSetting extends Setting
{
	
	public int keyCode;
	
	public KeyBindSetting(String name, Supplier<Boolean> visibility,
		int keyCode)
	{
		super(name, visibility, keyCode);
		this.name = name;
		this.keyCode = keyCode;
	}
	
	public KeyBindSetting(String name, int keyCode)
	{
		super(name, null, keyCode);
		this.name = name;
		this.keyCode = keyCode;
	}
	
}
