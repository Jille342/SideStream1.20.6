package client.features.modules.combat;

import client.event.listeners.EventAttack;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module{
    ModeSetting mode;

    public Criticals()
    {
        super("Criticals", 0, Module.Category.COMBAT);

    }
    public void init()
    {
        super.init();
        mode = new ModeSetting("Mode", "Matrix", "Matrix");
        addSetting(mode);
    }
    public void onUpdate(EventUpdate eventUpdate){
        setTag(mode.getValue());
    }
    public void onAttack(EventAttack event){
       sendFakeY(1.0E-5, mc.player.isOnGround());
       sendFakeY(0, mc.player.isOnGround());
    }
    private void sendFakeY(double offset, boolean onGround)
    {
        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + offset,
                        mc.player.getZ(), onGround));
    }
}
