package client.utils;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
public class WorldUtils {

    public static TimeHelper tpsCounter = new TimeHelper();

    public static double tps;

    public static void onTime(WorldTimeUpdateS2CPacket packet) {
        tps = Math.round(tpsCounter.getCurrentMS() - tpsCounter.getLastMS()) / 50;
        tpsCounter.reset();
    }

}
