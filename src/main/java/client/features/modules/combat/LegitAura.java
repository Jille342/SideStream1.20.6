
package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventInput;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class LegitAura extends Module
{
	
	NumberSetting CPS;
	BooleanSetting targetMonstersSetting;
	BooleanSetting targetAnimalsSetting;
	BooleanSetting ignoreTeamsSetting;
	
	NumberSetting rangeSetting;
	ModeSetting sortmode;
	BooleanSetting targetInvisibles;
	NumberSetting fov;
	BooleanSetting hitThroughWalls;
	BooleanSetting clickOnly;
	public static ModeSetting rotationmode;
	NumberSetting maxCPS;
	NumberSetting minCPS;
	BooleanSetting noInventoryAttack;
	BooleanSetting moveFix;
	BooleanSetting itemCheck;
	
	public LegitAura()
	{
		super("LegitAura", 0, Category.COMBAT);
	}
	
	@Override
	public void init()
	{
		this.rangeSetting = new NumberSetting("Range", 3.0, 0, 4.2, 0.1);
		this.targetMonstersSetting =
			new BooleanSetting("Target Monsters", true);
		this.targetInvisibles = new BooleanSetting("Target Invisibles", false);
		this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
		this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
		noInventoryAttack = new BooleanSetting("No Inventory Attack", true);
		this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
		minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);
		sortmode = new ModeSetting("SortMode", "Angle",
			new String[]{"Distance", "Angle"});
		rotationmode = new ModeSetting("Rotation Mode", "Normal",
			new String[]{"None", "Normal", "Normal2" ,"Legit"});
		moveFix = new BooleanSetting("Move Fix", true);
		itemCheck = new BooleanSetting("Item Check", true);
		this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
		hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
		clickOnly = new BooleanSetting("Click Only", true);

		addSetting(rotationmode, maxCPS, minCPS, targetAnimalsSetting,
			targetMonstersSetting, ignoreTeamsSetting, sortmode,
			targetInvisibles, fov, hitThroughWalls, rangeSetting, clickOnly, noInventoryAttack, moveFix, itemCheck);
		super.init();
	}
	
	ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
	private final TimeHelper attackTimer = new TimeHelper();
	LivingEntity target = null;
	
	@Override
	public void onEvent(Event<?> e)
	{
		
		if(e instanceof EventUpdate)
		{
			target = findTarget();
			setTag(sortmode.getMode() + " " + targets.size());
			if(target != null)
			{
				if(!(Objects.requireNonNull(mc.player).isUsingItem() && itemCheck.isEnabled())
					&& !(mc.currentScreen instanceof InventoryScreen
						&& noInventoryAttack.isEnabled()))
				{
					
					if(e.isPre())
					{
						
						if(target != null)
						{
							if(attackTimer.hasReached(calculateTime(
								minCPS.getValue(), maxCPS.getValue()))
								&& target.isAlive())
							{
								attack(target);
								attackTimer.reset();
							}
							
							if(!target.isAlive() || target.age < 10)
								targets.remove(target);
						}
					}
					
					super.onEvent(e);
				}
			}
		}
		if(e instanceof EventMotion)
		{
			if(mc.currentScreen instanceof InventoryScreen
				&& noInventoryAttack.isEnabled())
			{
				return;
			}
			if(target != null)
			{
				EventMotion event = (EventMotion)e;
				if(!targets.isEmpty())
				{
					
					if(!target.isAlive() || target == null)
						return;

					if(rotationmode.getMode().equalsIgnoreCase("Normal"))
					{
						float[] angles =
							RotationUtils.getRotationsEntity(target);
						float[] fixed = RotationUtils.fixedSensitivity(angles,mc.options.getMouseSensitivity().getValue().floatValue());
						event.setYaw(fixed[0]);
						event.setPitch(fixed[1]);
					}
					if(rotationmode.getMode().equalsIgnoreCase("Normal2"))
					{
						float[] angles = RotationUtils
							.getRotationsRandom((LivingEntity)target);
						float[] fixed = RotationUtils.fixedSensitivity(angles,mc.options.getMouseSensitivity().getValue().floatValue());

						event.setYaw(fixed[0]);
						event.setPitch(fixed[1]);
					}
					if(rotationmode.getMode().equalsIgnoreCase("Legit")){
						float[] angles = RotationUtils.calcRotation((LivingEntity)target);
						float[] fixed = RotationUtils.fixedSensitivity(angles,mc.options.getMouseSensitivity().getValue().floatValue());

						event.setYaw(fixed[0]);
						event.setPitch(fixed[1]);
					}
				}
			}
			
		}
		if(e instanceof EventInput){
			((EventInput) e).moveFix = moveFix.isEnabled();
		}
		
	}
	public void attack(Entity target){

		Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(
				PlayerInteractEntityC2SPacket.attack(target,
						Objects.requireNonNull(mc.player).isSneaking()));
		Objects.requireNonNull(mc.player).swingHand(Hand.MAIN_HAND);

	}
	
	private LivingEntity findTarget()
	{
		targets.clear();
		
		assert mc.world != null;
		for(Entity entity : mc.world.getEntities())
		{
			if(entity instanceof LivingEntity && entity != mc.player)
			{
				if(!entity.isAlive() || entity.age < 10)
				{
					continue;
				}
				if(clickOnly.enabled && !mc.options.attackKey.isPressed())
					continue;
				if(entity.isInvisible() && !targetInvisibles.enabled)
					continue;
				
				if(!RotationUtils.fov(entity, fov.value))
					continue;
				if(!mc.player.canSee(entity) && !hitThroughWalls.isEnabled())
					continue;
				double focusRange = rangeSetting.value;
				if(mc.player.distanceTo(entity) > focusRange)
					continue;
				if(entity instanceof PlayerEntity)
				{
					
					if(ignoreTeamsSetting.enabled
						&& ServerHelper.isTeammate((PlayerEntity)entity))
					{
						continue;
					}
					if(AntiBots.isBot((PlayerEntity)entity))
						continue;
					
					targets.add((LivingEntity)entity);
				}else if(entity instanceof AnimalEntity
					&& targetAnimalsSetting.enabled)
				{
					targets.add((LivingEntity)entity);
				}else if(entity instanceof MobEntity
					&& targetMonstersSetting.enabled)
				{
					targets.add((LivingEntity)entity);
				}
			}
		}
		
		if(targets.isEmpty())
			return null;
		switch(sortmode.getMode())
		{
			case "Distance":
			this.targets.sort(Comparator.comparingDouble(
				(entity) -> (double)mc.player.distanceTo((Entity)entity)));
			break;
			case "Angle":
			targets.sort(Comparator
				.comparingDouble(RotationUtils::calculateYawChangeToDst));
		}
		this.targets.sort(Comparator.comparingInt(o -> o.hurtTime));
		return targets.get(0);
	}
	
	private double calculateTime(double mincps, double maxcps)
	{
		double cps;
		if(mincps > maxcps)
			mincps = maxcps;
		cps = (client.utils.RandomUtils.nextInt((int)mincps, (int)maxcps)
			+ client.utils.RandomUtils.nextInt(-3, 3));
		if(cps > maxcps)
			cps = (int)maxcps;
		
		return((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1))
			+ 1000 / cps);
	}
	
	@Override
	public void onEnabled()
	{
		targets.clear();
		target = null;
		super.onEnabled();
	}
	@Override
	public void onDisabled()
	{
		targets.clear();
		target = null;
		super.onDisabled();
	}
}
