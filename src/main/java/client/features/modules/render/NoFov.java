package client.features.modules.render;

import client.features.modules.Module;
import client.settings.NumberSetting;

public class NoFov extends Module
{
	
	public NoFov()
	{
		super("NoFov", 0, Category.RENDER);
	}
	
	public void init()
	{
		super.init();
	}
	
	@Override
	public void onEnabled()
	{
		mc.options.getFovEffectScale().setValue(0D);
		super.onEnabled();
	}
	
	@Override
	public void onDisabled()
	{
		super.onDisabled();
	}
	
}
