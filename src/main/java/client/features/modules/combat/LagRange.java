package client.features.modules.combat;

import client.Client;
import client.event.Event;
import client.event.EventType;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.settings.NumberSetting;
import client.utils.RaytraceUtils;
import client.utils.RotationUtils;
import client.utils.TimeHelper;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.hit.EntityHitResult;
import java.util.*;

public class LagRange extends Module {
    public TimeHelper timer = new TimeHelper();

    public LinkedList<Packet> outPackets = new LinkedList();
    public NumberSetting pulseDelay = new NumberSetting("pulse Delay", 200.0, 10.0, 500.0, 10.0);
            ;public LagRange() {
        super("LagRange",0 ,Category.COMBAT);
        addSetting(pulseDelay);
    }
    public void onEvent(Event<?> event){
        if(event instanceof EventPacket){
            if(event.isOutgoing()){
                if (this.shouldCancel() && !event.isCancelled()) {
                    event.setCancelled(true);
                    outPackets.add(((EventPacket) event).getPacket());
                }

            }
        }
        if(event instanceof EventUpdate){
            this.setTag(String.valueOf(pulseDelay.getValue()));
                if (this.timer.hasReached(pulseDelay.getValue())) {
                    this.fullRelease();
                }
                if (Objects.requireNonNull(mc.player).hurtTime > 0) {
                    this.fullRelease();
                }
        }
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
    }

    @Override
    public void onDisabled() {
        if (mc.player == null) return;
        this.fullRelease();
        super.onDisabled();
    }

    public boolean isHurtTime() {
        return LegitAura2.target.hurtTime <= 2;
    }
    public void fullRelease() {
        if (!mc.isInSingleplayer()) {
            try {
                while(!outPackets.isEmpty()) {
            mc.getNetworkHandler().sendPacket((Packet)this.outPackets.poll());
                }
            } catch (Exception var2) {
            }
           outPackets.clear();
           timer.reset();
        }
    }

    public boolean shouldCancel() {
        if (ModuleManager.getModulebyClass(LegitAura2.class).isEnabled() && !mc.isInSingleplayer()) {
            return true;
        } else {
           fullRelease();
            return false;
        }
    }

    public boolean isTargetCloseOrVisible() {
        RaytraceUtils raytraceUtils = new RaytraceUtils();
        EntityHitResult rayTracedEntity = raytraceUtils.rayCastByRotation(RotationUtils.virtualYaw, RotationUtils.virtualPitch,3.0f);
        if (LegitAura2.target == null) {
            return false;
        } else {
            return rayTracedEntity.getEntity() == LegitAura2.target || mc.targetedEntity == LegitAura2.target;
        }
    }


}
