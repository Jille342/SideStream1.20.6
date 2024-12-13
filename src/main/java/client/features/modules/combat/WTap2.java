package client.features.modules.combat;

import java.util.Collections;

import client.event.listeners.EventAttack;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.text.Text;

public class WTap2 extends Module {
    private boolean tapping;
    private int tick;

    public WTap2() {
        super("WTap2", 0, Category.COMBAT);
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

    public void onAttack(EventAttack event) {
        if (mc.player.isSprinting() && !this.tapping) {
            mc.player.setSprinting(false);
            this.tapping = true;
            this.tick = 0;
        }

        super.onAttack(event);
    }

}
