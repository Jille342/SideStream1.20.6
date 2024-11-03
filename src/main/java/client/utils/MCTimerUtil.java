package client.utils;

import client.mixin.client.MinecraftClientAccessor;
import client.mixin.client.RenderTickCounterAccessor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MCTimerUtil implements MCUtil
{
	
	@Getter
    private static float timerSpeed = 1f;

    public static void setTimerSpeed(float timerSpeed)
	{
		MCTimerUtil.timerSpeed = timerSpeed;
		
		((RenderTickCounterAccessor)(((MinecraftClientAccessor)mc)
			.getRenderTickCounter())).setTickTime(1000f / timerSpeed / 20f);
	}
}
