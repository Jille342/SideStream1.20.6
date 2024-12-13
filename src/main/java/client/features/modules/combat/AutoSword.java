package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import java.util.Objects;

public class AutoSword extends Module
{
    public AutoSword()
    {
        super("AutoSword", 0, Category.COMBAT);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventPacket eventPacket) {
            if(eventPacket.isOutgoing()) {
                if (eventPacket.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {

                        float n = 0.0f;
                        for (int b1 = 0; b1 < 9; b1++) {
                            ItemStack itemStack =
                                    mc.player.getInventory().getStack(b1);
                            if (itemStack == null) {
                                continue;
                            }
                            if (itemStack.getItem() instanceof SwordItem swordItem ) {
                                final float a =getSwordValue(swordItem);
                                if (a >= n) {
                                    n = a;
                                    mc.player.getInventory().selectedSlot= b1;
                                }
                        }
                    }
                }
            }
        }
    }

    private static float getSwordValue(SwordItem item) {

        return item.getMaterial().getAttackDamage();
    }

}
