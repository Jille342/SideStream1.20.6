package client.utils;

import client.Client;
import client.features.modules.movement.DebugSpeed;
import client.mixin.client.MinecraftClientAccessor;
import client.mixin.client.RenderTickCounterAccessor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.SentMessage;

public class MCTimerUtil
{

    @Getter
    private static float timerSpeed = 1.0F;

    public static void setTimerSpeed(float timerSpeed)
	{
    MCTimerUtil.timerSpeed = timerSpeed;
      ((RenderTickCounterAccessor)((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter()).setTickTime(1000.0F / timerSpeed / 20.0F);

      //  Client.IMC.setTickSpeedMultiplier(timerSpeed);
    }

}
