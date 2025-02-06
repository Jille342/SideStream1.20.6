package client.features.modules.combat;

import client.event.listeners.EventAttack;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.utils.ChatUtils;
import client.utils.RotationUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module{
    ModeSetting mode;
    private boolean skipPacket;

    public Criticals()
    {
        super("Criticals", 0, Module.Category.COMBAT);

    }
    public void init()
    {
        super.init();
        mode = new ModeSetting("Mode", "Matrix", "Matrix","Packet");
        addSetting(mode);
    }
    public void onUpdate(EventUpdate eventUpdate){
        setTag(mode.getValue());
    }
    @Override
    public void onAttack(EventAttack event){
        if(mode.getMode().equalsIgnoreCase("Matrix")) {
            sendFakeY(1.0E-5, false);
            sendFakeY(0, true);
        } else if (mode.getMode().equalsIgnoreCase("Packet")) {

                sendFakeY(0.0625, true);
                sendFakeY(0, false);
                sendFakeY(1.1e-5, false);
                sendFakeY(0, false);
        }
    }
    private void sendFakeY(double offset, boolean onGround) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + offset, mc.player.getZ(), onGround));
    }
}
