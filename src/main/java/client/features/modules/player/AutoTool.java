
package client.features.modules.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool",0 ,Category.PLAYER);
    }

    public void onEvent(Event<?> event){
        if(event instanceof EventUpdate){
            if (!mc.options.attackKey.isPressed())
                return;
            if (mc.crosshairTarget == null || mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
                return;
            Vec3d blockPos = mc.crosshairTarget.getPos();
            if (blockPos == null)
                return;
            updateTool(blockPos);
        }
    }

    public void updateTool(Vec3d paramBlockPos) {
        assert mc.world != null;
        Block block = mc.world.getBlockState(BlockPos.ofFloored(paramBlockPos)).getBlock();
        float f = 1.0F;
        byte b = -1;
        for (byte b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = mc.player.getInventory().getStack(b1);
            ItemStack current = mc.player.getInventory().getMainHandStack();
            if (itemStack != null && (itemStack.getMiningSpeedMultiplier(block.getDefaultState())) > f && !(current.getMiningSpeedMultiplier(block.getDefaultState()) > f)) {
                f = itemStack.getMiningSpeedMultiplier(block.getDefaultState());
                b = b1;
            }
        }
        if (b != -1) {
            mc.player.getInventory().selectedSlot = b;
        }
    }
}
