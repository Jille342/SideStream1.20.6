package client.features.modules.movement;

import client.event.Event;
import client.features.modules.Module;

import java.util.Objects;

public class Sprint extends Module
{
	public Sprint()
	{
		super("Sprint", 0, Category.MOVEMENT);
	}
	
	public void onEvent(Event<?> e)
	{
			if (mc.player != null) {
				if (mc.player.forwardSpeed > 0) {
					mc.options.sprintKey.setPressed(true);
				}
			}
		}
	}
