package client.features.modules.combat;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.NumberSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LagRange extends Module {
    private final List<Packet<?>> packets = Collections.synchronizedList(new ArrayList<>());
    private final int preActivationBlocks = 2;
    private final NumberSetting packetsLag = new NumberSetting("Packet Delay", 2, 1, 20, 1);
    private final NumberSetting distanceEnemy = new NumberSetting("Enemy Distance", 5, 1, 10, 1);
    public LagRange() {
        super("LagRange",0 ,Category.COMBAT);
        addSetting(packetsLag, distanceEnemy);
    }
    public void onEvent(Event<?> event){
        if(event instanceof EventPacket){
            if(event.isOutgoing()){
                synchronized (packets) {
                    packets.add(((EventPacket) event).getPacket());
                }
                event.setCancelled(true);
            }
        }
        if(event instanceof EventUpdate){
            if (event.isPost()) {
                PlayerEntity enemy;
                if( AimAssist.primary != null ){
                    enemy = (PlayerEntity) AimAssist.primary;
                } else if(LegitAura2.target != null){
                    enemy = (PlayerEntity) LegitAura2.target;
                } else {
                    enemy = null;
                }

                if (enemy == null) {
                    if (!packets.isEmpty() && Objects.requireNonNull(mc.player).age % packetsLag.getValue() == 0) {
                        synchronized (packets) {
                            Packet<?> packet = packets.removeFirst();
                            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
                        }
                    }
                } else {
                    double distanceToEnemy = Objects.requireNonNull(mc.player).distanceTo(enemy);
                    if (distanceToEnemy <= distanceEnemy.getValue() + preActivationBlocks) {
                        synchronized (packets) {
                            Packet<?> packet = packets.removeFirst();
                            Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);

                            packets.clear();
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onEnabled() {
        reset();
        super.onEnabled();
    }

    @Override
    public void onDisabled() {
        if (mc.player == null) return;
        reset();
        super.onDisabled();
    }

    private void reset() {
        synchronized (packets) {
            packets.clear();
        }
    }

}
