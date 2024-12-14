package client.features.modules.combat;


import client.event.listeners.EventAttack;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.utils.ChatUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;

public class WTap2 extends Module {
    private boolean tapping;
    private int tick;
private BooleanSetting debug;
    public WTap2() {
        super("WTap2", 0, Category.COMBAT);
        debug = new BooleanSetting("Debug", false);
        addSetting(debug);
    }

    public void onUpdate(EventUpdate event) {
        if (this.tapping) {
            if (this.tick == 2) {
                mc.player.setSprinting(true);
                this.tapping = false;
            }

            ++this.tick;
        }

    }
@Override
    public void onAttack(EventAttack event) {
        if (mc.player.isSprinting() && !this.tapping) {
            mc.player.setSprinting(false);
            this.tapping = true;
            this.tick = 0;
        }

    }


}
