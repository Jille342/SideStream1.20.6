package client.features.modules.misc;

import java.util.Objects;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.RaycastUtils;
import client.utils.RotationUtils;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CivBreak extends Module
{
	ModeSetting mode;
	NumberSetting range;
	NumberSetting packetDelay;
	private BlockPos blockPos;
	private HitResult hitResult;
	private int attempt;
	
	public CivBreak()
	{
		super("CivBreak", 0, Category.MISC);
	}
	
	public void init()
	{
		super.init();
		this.range = new NumberSetting("Range", 5.0, 4.5, 7.0, 0.1);
		mode =
			new ModeSetting("Mode", "Legit", new String[]{"Legit", "Packet"});
		packetDelay = new NumberSetting("Packet Delay", 20, 10, 200, 1.0);
		addSetting(range, mode, packetDelay);
	}
	
	// public void onClickTick(final ClickTickEvent event) {
	// if (this.blockPos != null || this.hitResult != null)
	// event.cancel();
	
	// super.onClickTick(event);
	// }
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventUpdate)
		{
			RaycastUtils raycastUtils = new RaycastUtils();
			setTag(mode.getMode());
			BlockPos nexus = getNexus();
			if( nexus == null)
				return;
			hitResult = raycastUtils.rayCast(getAngleToBlockPos(nexus),Math.sqrt(nexus.getSquaredDistance(mc.player.getEyePos())), mc.getTickDelta());
			if(hitResult == null)
				return;
			Direction facing = ((BlockHitResult)this.hitResult).getSide();
				blockPos = nexus;
				switch(this.mode.getMode())
				{
					
					case "Packet":
					
					if(facing == null)
						return;
					
					if(this.blockPos != null)
					{
						final float f =
							(float)(mc.player.getX() - this.blockPos.getX());
						final float g =
							(float)(mc.player.getY() - this.blockPos.getY());
						final float h =
							(float)(mc.player.getZ() - this.blockPos.getZ());
						final float dist =
							MathHelper.sqrt(f * f + g * g + h * h);
						
						if(dist >= this.range.getValue())
						{
							this.hitResult = null;
							this.blockPos = null;
							return;
						}
						
						mc.player.swingHand(Hand.MAIN_HAND);
						for(int i =
							0; i < (int)this.packetDelay.getValue(); i++)
						{
							mc.player.networkHandler
								.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
									this.blockPos, facing, 0));
						}
						
						mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
								PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
								this.blockPos, facing, 0));
					}
					break;
					case "Legit":
					if(facing == null)
					{
						return;
					}
					final float f =
						(float)(mc.player.getX() - this.blockPos.getX());
					final float g =
						(float)(mc.player.getY() - this.blockPos.getY());
					final float h =
						(float)(mc.player.getZ() - this.blockPos.getZ());
					final float dist = MathHelper.sqrt(f * f + g * g + h * h);
					
					if(dist >= this.range.getValue())
					{
						this.hitResult = null;
						this.blockPos = null;
						this.attempt = 0;
						return;
					}
					mc.player.networkHandler
						.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
					if(!mc.interactionManager.isBreakingBlock())
					mc.interactionManager.updateBlockBreakingProgress(blockPos, facing);

					break;
				}
			
		}
		if(event instanceof EventMotion)
		{
			if(this.blockPos == null)
				return;
			final float[] angles = getAngleToBlockPos(this.blockPos);
			((EventMotion)event).setYaw(angles[0]);
			((EventMotion)event).setPitch(angles[1]);
			
		}
	}
	
	public BlockPos getNexus()
	{
		BlockPos pos = null;
		for(int x = -7; x < 7; x++)
		{
			for(int y = -7; y < 7; y++)
			{
				for(int z = -7; z < 7; z++)
				{
					pos = new BlockPos((int)(mc.player.getX() + x),
						(int)(mc.player.getY() + y),
						(int)(mc.player.getZ() + z));
					if(Objects.requireNonNull(mc.world).getBlockState(pos).getBlock() == Blocks.END_STONE)
						return pos;
				}
			}
		}
		return null;
	}
	
	private float[] getAngleToBlockPos(final BlockPos pos)
	{
		final float[] angle = this.calcAngle(mc.player.getEyePos(),
			new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
		return angle;
	}
	
	private float[] calcAngle(final Vec3d from, final Vec3d to)
	{
		final double difX = to.x - from.x;
		final double difY = (to.y - from.y) * -1.0;
		final double difZ = to.z - from.z;
		final double dist = Math.sqrt((float)(difX * difX + difZ * difZ));
		return new float[]{
			(float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0),
			(float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
	}@Override
	public void onEnabled()
	{
		// for (int i = 0; i < 20; i++) {
		// mc.getNetworkHandler().sendChatMessage("/rank " +
		// RandomStringUtils.random(12,
		// "abcdefghijklmnopqrstuvwxyz0123456789"));
		// }
		this.attempt = 0;
		super.onEnabled();
	}
	
	@Override
	public void onDisabled()
	{
		this.blockPos = null;
		this.hitResult = null;
		super.onDisabled();
	}
}
