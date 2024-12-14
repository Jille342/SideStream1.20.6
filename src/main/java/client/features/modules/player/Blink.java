package client.features.modules.player;

import client.event.listeners.EventUpdate;
import client.utils.CopiedOtherClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * @author Tigermouthbear
 */
public class Blink extends client.features.modules.Module {
    private final Queue<PlayerMoveC2SPacket> queue = new LinkedList<>();
    private CopiedOtherClientPlayerEntity clone;
    public Blink() {
        super("Blink",0,Category.PLAYER);
    }

    public void onEnabled() {
        if(mc.player != null) {
            clone = new CopiedOtherClientPlayerEntity(mc.world, mc.player);
            Objects.requireNonNull(mc.world).addEntity(clone);
        }
    }@Override
    public void onDisabled() {
        while(!queue.isEmpty()) Objects.requireNonNull(mc.player).networkHandler.sendPacket(queue.poll());

        if(mc.player != null) {
            Objects.requireNonNull(mc.world).removeEntity(clone.getId(), Entity.RemovalReason.DISCARDED);
            clone = null;
        }
    }
    public void onUpdate(EventUpdate event){
        setTag(String.valueOf(queue.size()));
    }
}