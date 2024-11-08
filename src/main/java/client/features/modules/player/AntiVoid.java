package client.features.modules.player;

import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class AntiVoid extends Module {
    public AntiVoid() {
        super("AntiVoid",0, Category.PLAYER);
    }

    public void onUpdate(EventUpdate event) {
        if (event.isPre() && Objects.requireNonNull(mc.player).fallDistance > 7.5F && Objects.requireNonNull(mc.world).getBlockState(new BlockPos((int) mc.player.getX(), 0, (int) mc.player.getY())).getBlock() == Blocks.AIR) {
            boolean isVoid = true;

            for(int i = 0; i < 50; ++i) {
                if (mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) (mc.player.getY() - (double)i), (int) mc.player.getZ())).getBlock() != Blocks.AIR) {
                    isVoid = false;
                }
            }

            if (isVoid) {
                mc.player.setVelocity(mc.player.getVelocity().x, 1, mc.player.getVelocity().z);
            }
        }

    }
}
