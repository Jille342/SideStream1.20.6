package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

/**
 * Created by cool1 on 1/19/2017.
 */
public class TPBreaker extends Module
{
	
	public static BlockPos blockBreaking;
	private double xPos, yPos, zPos, minx;
	float[] rotations = null;
	
	ModeSetting mode;
	NumberSetting radius1;
	
	public TPBreaker()
	{
		super("TPBreaker", 0, Category.MISC);
	}
	
	@Override
	public void onDisabled()
	{
		blockBreaking = null;
		super.onDisabled();
	}
	
	public void init()
	{
		mode =
			new ModeSetting("Mode", "RightClick", new String[]{"RightClick"});
		this.radius1 = new NumberSetting("Radius", 5, 1, 10, 1f);
		addSetting(mode, radius1);
		super.init();
		
	}
	
	@Override
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventUpdate)
		{
			
			if(mode.getMode().equals("RightClick"))
			{
				if(mc.world == null || mc.player == null)
					return;
				BlockPos tpPos = getNextBlock();
				if(tpPos != null)
				{
					setTag(mode.getMode() + " 1");
					xPos = tpPos.getX();
					yPos = tpPos.getY();
					zPos = tpPos.getZ();
					// ChatUtils.printChat(block.getName().getString());
					placeBlock(tpPos);
					rotations = getBlockRotations(tpPos.getX(), tpPos.getY(),
						tpPos.getZ());
				}else
				{
					setTag(mode.getMode());
				}
				
			}
		}
		if(event instanceof EventMotion)
		{
			if(rotations != null)
			{
				EventMotion emm = (EventMotion)event;
				emm.setYaw(rotations[0]);
				emm.setPitch(rotations[1]);
			}
		}
	}
	
	// private boolean blockChecks(Block block) {
	// return block == Blocks.quartz_ore;
	// }
	
	public float[] getBlockRotations(double x, double y, double z)
	{
		double var4 = x - mc.player.getX() + 0.5;
		double var5 = z - mc.player.getZ() + 0.5;
		double var6 = y - (mc.player.getY()
			+ mc.player.getEyeHeight(mc.player.getPose()) - 1.0);
		double var7 = Math.sqrt(var4 * var4 + var5 * var5);
		float var8 = (float)(Math.atan2(var5, var4) * 180.0 / Math.PI) - 90.0f;
		return new float[]{var8,
			(float)(-(Math.atan2(var6, var7) * 180.0 / Math.PI))};
	}
	
	private BlockPos getNextBlock()
	{
		// Scan to find next block to begin breaking.
		int rad = (int)radius1.getValue();
		for(int y = rad; y > -rad; y--)
		{
			for(int x = -rad; x < rad; x++)
			{
				for(int z = -rad; z < rad; z++)
				{
					BlockPos blockpos = new BlockPos(
						Objects.requireNonNull(mc.player).getBlockX() + x,
						(int)mc.player.getBlockY() + y,
						(int)mc.player.getBlockZ() + z);
					Block block = Objects.requireNonNull(mc.world)
						.getBlockState(blockpos).getBlock();
					if(block == Blocks.AIR)
						continue;
					if(block == Blocks.NETHER_QUARTZ_ORE)
					{
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
			Direction side2 = side.getOpposite();
			BlockHitResult blockHitResult =
					new BlockHitResult(pos.toCenterPos(), side2, pos, false).withBlockPos(pos);
			Objects.requireNonNull(mc.getNetworkHandler())
				.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
			Objects.requireNonNull(mc.player).networkHandler
				.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
					blockHitResult, 0));
			Objects.requireNonNull(mc.interactionManager).updateBlockBreakingProgress(pos,side2);
		}
	}
}
