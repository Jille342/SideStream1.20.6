package client.features.modules.render;

import client.features.modules.Module;
import client.ui.clicckgui.ClickGui;

public class ClickGUI extends Module
{
	
	public ClickGUI()
	{
		super("ClickGUI", 310, Category.RENDER);
	}
	
	@Override
	public void init()
	{
		super.init();
	}

	
	@Override
	public void onEnabled()
	{
		mc.setScreen(new ClickGui());
		toggle();
		// mc.setScreen(new GuiClickGUI(0));
		super.onEnabled();
	}
}
