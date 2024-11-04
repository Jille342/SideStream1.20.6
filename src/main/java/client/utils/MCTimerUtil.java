package client.utils;

import client.mixin.client.MinecraftClientAccessor;
import client.mixin.client.RenderTickCounterAccessor;
import lombok.Getter;

public class MCTimerUtil implements MCUtil
{
	
	@Getter
    private static float timerSpeed = 1f;

    public static void setTimerSpeed(float timerSpeed)
	{

        ((RenderTickCounterAccessor)(((MinecraftClientAccessor)mc)
			.getRenderTickCounter())).setTickTime(1000f / timerSpeed / 20f);
	}
}
