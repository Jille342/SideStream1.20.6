package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventRenderGUI;
import client.features.modules.Module;
public class ClickGUI extends Module {

	public ClickGUI() {
		super("ClickGUI", 310,	Category.RENDER);
	}



	@Override
	public void init() {
		super.init();
	}

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventRenderGUI) {

		}
		super.onEvent(e);
	}

	@Override
	public void onEnable() {
	//	mc.setScreen(new GuiClickGUI(0));
		super.onEnable();
	}
}
