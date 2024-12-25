package client.features.modules.misc;

import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import net.minecraft.entity.effect.StatusEffects;

public class AntiDebuff  extends Module {
    public AntiDebuff() {
        super("AntiDebuff", 0, Category.MISC);
    }
    public void onUpdate(EventUpdate eventUpdate){
        if (mc.player != null) {
            mc.player.removeStatusEffectInternal(StatusEffects.BLINDNESS);
            mc.player.removeStatusEffectInternal(StatusEffects.NAUSEA);
            mc.player.removeStatusEffectInternal(StatusEffects.WITHER);
            mc.player.removeStatusEffectInternal(StatusEffects.DARKNESS);
        }
    }

}
