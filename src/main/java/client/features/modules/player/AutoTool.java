
package client.features.modules.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool",0 ,Category.PLAYER);
    }
    public int slot;

    public void onEvent(Event<?> event){
        if(event instanceof EventUpdate){
            if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && !mc.world.getBlockState(blockHitResult.getBlockPos()).isAir()) {
                int n = getTool(blockHitResult.getBlockPos());
                if (n != -1 && mc.options.attackKey.isPressed()) {
                    slot = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot =n;
                } else {
                }
            }
        }
    }
    private int getTool(BlockPos blockPos) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (!itemStack.isEmpty() && itemStack.getMaxDamage() - itemStack.getDamage() > 10) {
                float f = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, itemStack);
                float f2 = itemStack.getMiningSpeedMultiplier(mc.world.getBlockState(blockPos));
                if (mc.world.getBlockState(blockPos).getBlock() instanceof AirBlock) {
                    return -1;
                }
                if (f + f2 > 1) {
                    return i;
                }
            }
        }
        return -1;
    }
}
