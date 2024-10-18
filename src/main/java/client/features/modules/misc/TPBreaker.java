package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Created by cool1 on 1/19/2017.
 */
public class TPBreaker extends Module {

    public static BlockPos blockBreaking;
    private double xPos, yPos, zPos, minx;

    ModeSetting mode;
    NumberSetting radius1;
    public TPBreaker() {
        super("TPBreaker", 0, Category.MISC);
    }

    @Override
    public void onDisable() {
        blockBreaking = null;
        super.onDisable();
    }

    public void init() {
        mode = new ModeSetting("Mode", "RightClick", new String[]{"RightClick"});
        this.radius1 = new NumberSetting("Radius", 5, 1, 10, 1f);
        addSetting(mode, radius1);
        super.init();

    }

    @Override
    public void onEvent(Event<?> event) {
        if (event instanceof EventUpdate) {
            setTag(mode.getMode());
            if (mode.getMode().equals("RightClick")) {
                if (mc.world == null || mc.player == null)
                    return;

                if (getNextBlock() != null) {
                    xPos = getNextBlock().getX();
                    yPos = getNextBlock().getY();
                    zPos = getNextBlock().getZ();
                    Block block = mc.world.getBlockState(getNextBlock()).getBlock();
                    if (block == Blocks.AIR || block == null)
                        return;
                    ChatUtils.printChat(block.getName().getString());
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                    placeBlock(getNextBlock());
                }

            }
        }
        //   if (event instanceof EventMotion) {
        //      if (getNextBlock() != null) {
        //        EventMotion emm = (EventMotion) event;
         //   float[] rotations = getBlockRotations(getNextBlock().getX(),getNextBlock().getY(),getNextBlock().getZ());
        //    emm.setYaw(rotations[0]);
        //  emm.setPitch(rotations[1]);
        //}ne

    }


//    private boolean blockChecks(Block block) {
  //      return block == Blocks.quartz_ore;
    //}

    public float[] getBlockRotations(double x, double y, double z) {
        double var4 = x - mc.player.getX() + 0.5;
        double var5 = z - mc.player.getZ() + 0.5;
        double var6 = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - 1.0);
        double var7 = Math.sqrt(var4 * var4 + var5 * var5);
        float var8 = (float) (Math.atan2(var5, var4) * 180.0 / Math.PI) - 90.0f;
        return new float[]{var8, (float) (-(Math.atan2(var6, var7) * 180.0 / Math.PI))};
    }

    private BlockPos getNextBlock() {
        // Scan to find next block to begin breaking.
        int rad = (int) radius1.getValue();
        for (int y = rad; y > -rad; y--) {
            for (int x = -rad; x < rad; x++) {
                for (int z = -rad; z < rad; z++) {
                    BlockPos blockpos = new BlockPos(mc.player.getBlockX() + x, (int) mc.player.getBlockY() + y,
                            (int) mc.player.getBlockZ() + z);
                    Block block = mc.world.getBlockState(blockpos).getBlock();
                    if (block == Blocks.AIR )
                        continue;
if(block== Blocks.NETHER_QUARTZ_ORE) {
    return blockpos;
}
                }
            }
        }
        return null;
    }
    private void placeBlock(BlockPos pos)
    {

        for(Direction side : Direction.values())
        {
            BlockPos neighbor = pos.offset(side);
            Direction side2 = side.getOpposite();

            Vec3d hitVec = Vec3d.ofCenter(neighbor)
                    .add(Vec3d.of(side2.getVector()).multiply(0.5));



            BlockHitResult hitResult = new BlockHitResult(hitVec, side2, neighbor, false);
           // mc.interactionManager.interactBlock(mc.player,Hand.MAIN_HAND, hitResult);
            sendSequencedPacket(id -> new PlayerInteractBlockC2SPacket( Hand.MAIN_HAND, hitResult, id));
        }

    }

    protected void sendSequencedPacket(SequencedPacketCreator packetCreator) {
         final PendingUpdateManager pendingUpdateManager = new PendingUpdateManager();
        if (mc.getNetworkHandler() == null || mc.world == null) return;
        try (pendingUpdateManager) {
            int i = pendingUpdateManager.getSequence();
            mc.getNetworkHandler().sendPacket(packetCreator.predict(i));
        }
    }

}