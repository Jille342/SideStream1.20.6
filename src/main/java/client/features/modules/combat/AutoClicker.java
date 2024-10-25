
package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.NumberSetting;
import client.utils.RandomUtils;
import client.utils.TimeHelper;
import net.minecraft.block.AirBlock;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class AutoClicker extends Module
{
	
	private final TimeHelper timer = new TimeHelper();
	private int breakTick;
	public static double cps;
	NumberSetting minCPS;
	NumberSetting maxCPS;
	private double currentCPS;
	
	public AutoClicker()
	{
		super("Auto Clicker", 0, Category.COMBAT);
		
	}
	
	@Override
	public void init()
	{
		super.init();
		this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
		minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);
		addSetting(maxCPS, minCPS);
	}
	
	@Override
	public void onDisabled()
	{
		breakTick = 0;
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			if(mc.options.attackKey.isPressed() && shouldClick())
			{
				doLeftClick();
			}
			
		}
	}
	
	private void doLeftClick()
	{
		if (this.currentCPS == 0) {
			this.currentCPS = 1;
		}
		if(timer.hasReached(1000/currentCPS))
		{
			currentCPS = RandomUtils.nextDouble(this.minCPS.getValue(), this.maxCPS.getValue());
			timer.reset();
			legitAttack();
		}
	}
	
	public void legitAttack()
	{

		if(mc.crosshairTarget == null || Objects.requireNonNull(mc.player).isRiding()
			|| mc.crosshairTarget.getType() == null)
		{
			return;
		}
		
		if(mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
		{
			mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
		}
		mc.player.swingHand(Hand.MAIN_HAND);
	}

	public boolean shouldClick()
	{
		if(!mc.isWindowFocused())
		{
			return false;
		}
		
		if(mc.player.isUsingItem())
		{
			return false;
		}
		
		if(mc.crosshairTarget != null )
		{
			if(mc.crosshairTarget
				.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK)
			{
				BlockHitResult blockHitResult =
					(BlockHitResult)mc.crosshairTarget;
				BlockPos blockPos = blockHitResult.getBlockPos();
				Block block = Objects.requireNonNull(mc.world)
					.getBlockState(blockPos).getBlock();
				if(block instanceof AirBlock)
				{
					return true;
				}
				
				if(mc.options.attackKey.isPressed())
				{
					if(breakTick > 1)
					{
						return false;
					}
					breakTick++;
				}else
				{
					breakTick = 0;
				}
			}else
			{
				breakTick = 0;
				
			}
		}
		return true;
	}
}
