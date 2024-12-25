package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventAttack;
import client.event.listeners.EventPacket;
import client.features.modules.Module;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
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
  if(e instanceof EventAttack){

                        float n = 0.0f;
                        for (int b1 = 0; b1 < 9; b1++) {
                            ItemStack itemStack =
                                    mc.player.getInventory().getStack(b1);
                            if (itemStack == null) {
                                continue;
                            }
                            if (itemStack.getItem() instanceof SwordItem swordItem ) {
                                final float a =getSwordValue(itemStack,swordItem);
                                if (a >= n) {
                                    n = a;
                                    mc.player.getInventory().selectedSlot= b1;
                                }
                        }
                    }
                }
            }

    private static float getSwordValue(ItemStack stack, SwordItem item) {
        float value = item.getMaterial().getAttackDamage() * 1000.0F;
        value += (float) EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
        value += (float)EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack) * 1000.0F;
        value += (float) EnchantmentHelper.getLevel(Enchantments.KNOCKBACK, stack);
        return value;
    }

}
