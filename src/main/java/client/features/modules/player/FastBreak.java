package client.features.modules.player;

import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class FastBreak extends Module {
    private final NumberSetting speed;

    public FastBreak() {
        super("FastBreak", 0, Category.PLAYER);
        speed = new NumberSetting("Speed", 8, 0, 10, 0.1);
        addSetting(speed);
    }

    public void onUpdate(EventUpdate event) {
        setTag(speed.getValueString());
        if (mc.crosshairTarget != null) {
            HitResult var3 = mc.crosshairTarget;
            if (var3 instanceof BlockHitResult) {
                BlockHitResult block = (BlockHitResult)var3;
                if (!mc.world.getBlockState(block.getBlockPos()).isAir() && mc.world.getBlockState(block.getBlockPos()).getBlock() != Blocks.END_STONE) {
                    if (mc.interactionManager.getBlockBreakingProgress() > (int)this.speed.getValue()) {
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, block.getBlockPos(), block.getSide()));
                        mc.world.setBlockState(block.getBlockPos(), Blocks.AIR.getDefaultState());
                    }

                }
            }
        }

    }
}
