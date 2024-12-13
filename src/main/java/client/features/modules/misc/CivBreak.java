package client.features.modules.misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender3D;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.RaycastUtils;
import client.utils.RenderingUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class CivBreak extends Module
{
	ModeSetting mode;
	NumberSetting range;
	NumberSetting packetDelay;
	private BlockPos blockPos;
	private HitResult hitResult;
	private int attempt;
	private  ArrayList<Packet> packets = new ArrayList();
	private boolean recoding = false;
	private ItemStack stack = null;



	public CivBreak()
	{
		super("CivBreak", 0, Category.MISC);
	}
	
	public void init()
	{
		super.init();
		range = new NumberSetting("Range", 5.0, 4.5, 7.0, 0.1);
		mode =
			new ModeSetting("Mode", "Legit", new String[]{"Legit", "Packet"});
		packetDelay = new NumberSetting("Packet Delay", 1, 1, 20, 1.0);
		addSetting(range, mode, packetDelay);
	}
	
	// public void onClickTick(final ClickTickEvent event) {
	// if (blockPos != null || hitResult != null)
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
			Direction facing = ((BlockHitResult)hitResult).getSide();
				blockPos = nexus;
				switch(mode.getMode())
				{
					case "Legit":
					if(facing == null)
					{
						return;
					}
					final float f =
						(float)(mc.player.getX() - blockPos.getX());
					final float g =
						(float)(mc.player.getY() - blockPos.getY());
					final float h =
						(float)(mc.player.getZ() - blockPos.getZ());
					final float dist = MathHelper.sqrt(f * f + g * g + h * h);
					
					if(dist > range.getValue())
					{
						hitResult = null;
						attempt = 0;
						blockPos = null;
						return;
					}
					mc.player.networkHandler
						.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
					if(!Objects.requireNonNull(mc.interactionManager).isBreakingBlock())
					mc.interactionManager.updateBlockBreakingProgress(blockPos, facing);
					break;
					case "Packet":
						if (this.blockPos == null) {
							this.blockPos = getNexus();
						}

						if (this.blockPos != null) {
							float f2= (float)(mc.player.getX() - (double)this.blockPos.getX());
							float g2 = (float)(mc.player.getY() - (double)this.blockPos.getY());
							float h2 = (float)(mc.player.getZ() - (double)this.blockPos.getZ());
							float dist2= MathHelper.sqrt(f2 * f2 + g2 * g2 + h2 * h2);
							if ((double)dist2 >= this.range.getValue()) {
								this.blockPos = null;
							} else {
								if (this.blockPos != null) {
									if (this.stack != mc.player.getMainHandStack()) {
										this.stack = mc.player.getMainHandStack();
										this.recoding = true;
										this.packets.clear();
									}

									if (this.recoding) {
										for(int i = 0; (double)i < this.packetDelay.getValue(); ++i) {
											mc.interactionManager.updateBlockBreakingProgress(this.blockPos, Direction.UP);
											mc.player.swingHand(Hand.MAIN_HAND);
										}
									} else if (mc.player.age % 2 == 0) {
										ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
										Objects.requireNonNull(networkHandler);
										packets.forEach(networkHandler::sendPacket);
									}
								}

							}
						}

						break;
				}
			
		}
		if(event instanceof EventMotion) {
			if (blockPos != null) {
				final float[] angles = getAngleToBlockPos(blockPos);
				((EventMotion) event).setYaw(angles[0]);
				((EventMotion) event).setPitch(angles[1]);
			}
		}
		if(event instanceof EventRender3D) {
			MatrixStack stack = ((EventRender3D) event).getMatrix();
			if (blockPos != null) {
				int color = -1;

				Box box = new Box(blockPos);
			if (Math.sqrt( Objects.requireNonNull(mc.player).squaredDistanceTo(blockPos.toCenterPos())) > range.getValue()) {
					color = new Color(1.0F, 0.0F, 0.0F, 0.11F).getRGB();
				} else {
				color = new Color(0, 200, 255, 158).getRGB();
				}
				RenderingUtils.draw3DBox2(stack.peek().getPositionMatrix(), box,color);
			}

		}
		if(event instanceof EventPacket){
			if(event.isOutgoing()){
				if (this.recoding) {
					Packet var3 = ((EventPacket) event).getPacket();
					if (var3 instanceof PlayerActionC2SPacket) {
						PlayerActionC2SPacket packet = (PlayerActionC2SPacket)var3;
						this.packets.add(var3);
						if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
							this.recoding = false;
						}
					}

				}

			}
		}
	}
	
	public BlockPos getNexus()
	{
		BlockPos pos;
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
        return calcAngle(mc.player.getEyePos(),
            new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
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
		attempt = 0;
		super.onEnabled();
	}
	
	@Override
	public void onDisabled()
	{
		blockPos = null;
		hitResult = null;
		super.onDisabled();
	}
}
