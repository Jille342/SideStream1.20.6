package client.features.modules.misc;

import client.Client;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import client.utils.ChatUtils;
import client.utils.RandomUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

import java.util.Objects;
import java.util.regex.Pattern;

public class AntiAFK extends Module {
    public boolean isAFK = false;
    public AntiAFK() {
        super("Anti AFK",0, Category.MISC);
    }
    public void onMotion(EventMotion eventMotion){
        if(mc.player != null && isAFK){
            double pitchRandom = RandomUtils.nextDouble(-5.0,5.0);
         eventMotion.setYaw((float) (eventMotion.getYaw() + pitchRandom));
         isAFK = false;
        }
    }
    public void onPacket(EventPacket eventPacket){
        if(eventPacket.isIncoming())
        {
            Packet<?> p = eventPacket.getPacket();
            if(p instanceof ChatMessageS2CPacket packet)
            {
                if(Objects.requireNonNull(stripper(Objects.requireNonNull(packet.unsignedContent())
                        .getLiteralString())).contains("You have been detected as idle and will be kicked"))
                {
                 isAFK = true;
                }
            }

        }
    }
    private final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public String stripper(String input){
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }
}
