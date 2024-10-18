/*
 * Copyright © 2014 - 2018 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BowAimbot extends Module
{
	private static final Box TARGET_BOX =
		new Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
	
	private Entity target;
	private float velocity;
	
	private final List<Entity> validated = new ArrayList<>();
	
	BooleanSetting targetMonstersSetting;
	BooleanSetting targetAnimalsSetting;
	BooleanSetting ignoreTeamsSetting;
	NumberSetting fov;
	
	public BowAimbot()
	{
		super("BowAimbot", 0, Module.Category.COMBAT);
	}
	
	public void init()
	{
		super.init();
		this.targetMonstersSetting =
			new BooleanSetting("Target Monsters", true);
		this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
		this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
		this.fov = new NumberSetting("FOV", 40.0D, 15.0D, 360.0D, 1.0D);
		
		addSetting(ignoreTeamsSetting, targetAnimalsSetting,
			targetMonstersSetting, fov);
	}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			// check if using item
			if(!mc.options.useKey.isPressed())
			{
				target = null;
				return;
			}
			
			// check if item is bow
			ItemStack item = mc.player.getInventory().getMainHandStack();
			if(item == null || !(item.getItem() instanceof BowItem))
			{
				return;
			}
			
			// set target
			
			target = this.findTarget();
			if(target == null)
				return;
			
			// set velocity
			velocity = (72000 - mc.player.getItemUseTime()) / 20F;
			velocity = (velocity * velocity + velocity * 2) / 3;
			if(velocity > 1)
				velocity = 1;
			
			// adjust for FastBow
			
			// set position to aim at
			double d = RotationUtils.getEyesPos()
				.distanceTo(target.getBoundingBox().getCenter());
			double posX = target.getX() + (target.getX() - target.prevX) * d
				- mc.player.getX();
			double posY = target.getY() + (target.getY() - target.prevY) * d
				+ target.getHeight() * 0.5 - mc.player.getY()
				- mc.player.getEyeHeight(target.getPose());
			double posZ = target.getZ() + (target.getZ() - target.prevZ) * d
				- mc.player.getZ();
			
			// set yaw
			mc.player
				.setYaw((float)Math.toDegrees(Math.atan2(posZ, posX)) - 90);
			
			// calculate needed pitch
			double hDistance = Math.sqrt(posX * posX + posZ * posZ);
			double hDistanceSq = hDistance * hDistance;
			float g = 0.006F;
			float velocitySq = velocity * velocity;
			float velocityPow4 = velocitySq * velocitySq;
			float neededPitch = (float)-Math
				.toDegrees(Math.atan((velocitySq - Math.sqrt(velocityPow4
					- g * (g * hDistanceSq + 2 * posY * velocitySq)))
					/ (g * hDistance)));
			
			// set pitch
			if(Float.isNaN(neededPitch))
				RotationUtils.faceEntityClient(target);
			else
				mc.player.setPitch(neededPitch);
		}
		super.onEvent(e);
	}
	
	public float calculateYawChangeToDst(Entity entity)
	{
		double diffX = entity.getX() - mc.player.getX();
		double diffZ = entity.getZ() - mc.player.getZ();
		double deg = Math.toDegrees(Math.atan(diffZ / diffX));
		if(diffZ < 0.0 && diffX < 0.0)
		{
			return (float)MathHelper
				.wrapDegrees(-(mc.player.getYaw() - (90 + deg)));
		}else if(diffZ < 0.0 && diffX > 0.0)
		{
			return (float)MathHelper
				.wrapDegrees(-(mc.player.getYaw() - (-90 + deg)));
		}else
		{
			return (float)MathHelper.wrapDegrees(-(mc.player.getYaw()
				- Math.toDegrees(-Math.atan(diffX / diffZ))));
		}
	}
	
	private Entity findTarget()
	{
		validated.clear();
		
		for(Entity entity : mc.world.getEntities())
		{
			if(entity != mc.player)
			{
				if(!entity.isAlive() || entity.age < 10)
				{
					continue;
				}
				assert mc.player != null;
				if(!mc.player.canSee(entity))
					continue;
				
				if(!PlayerHelper.fov(entity, fov.value))
					continue;
				if(entity instanceof PlayerEntity)
				{
					if(AntiBots.isBot((PlayerEntity)entity))
					{
						continue;
					}
					if(ignoreTeamsSetting.enable
						&& ServerHelper.isTeammate((PlayerEntity)entity))
					{
						continue;
					}
					
					validated.add(entity);
				}else if(entity instanceof AnimalEntity
					&& targetAnimalsSetting.enable)
				{
					validated.add(entity);
				}else if(entity instanceof MobEntity
					&& targetMonstersSetting.enable)
				{
					validated.add(entity);
				}
			}
		}
		
		if(validated.isEmpty())
			return null;
		validated
			.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
		return validated.get(0);
	}
	
}
