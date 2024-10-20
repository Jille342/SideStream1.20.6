package client.utils;

import client.mixin.client.MinecraftClientAccessor;
import client.mixin.client.RenderTickCounterAccessor;

public class MCTimerUtil implements MCUtil
{
	
	private static float timerSpeed = 1f;
	
	public static float getTimerSpeed()
	{
		return timerSpeed;
	}
	
	public static void setTimerSpeed(float timerSpeed)
	{
		MCTimerUtil.timerSpeed = timerSpeed;
		
		((RenderTickCounterAccessor)(((MinecraftClientAccessor)mc)
			.getRenderTickCounter())).setTickTime(1000f / timerSpeed / 20f);
	}
}
