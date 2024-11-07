package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.RandomUtils;
import client.utils.RotationUtils;
import client.utils.ServerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AimAssist extends Module
{
	
	private final List<LivingEntity> targets = new ArrayList<>();
	public static Entity target = null;
	BooleanSetting ignoreTeamsSetting;
	BooleanSetting notHolding;
	NumberSetting aimSpeedSetting;
	NumberSetting rangeSetting;
	BooleanSetting targetMonstersSetting;
	BooleanSetting targetAnimalsSetting;
	NumberSetting fov;
	ModeSetting sortmode;
	BooleanSetting ignoreBreaking;
	
	public AimAssist()
	{
		super("Aim Assist", 0, Category.COMBAT);
	}
	
	@Override
	public void init()
	{
		super.init();
		this.targetMonstersSetting =
			new BooleanSetting("Target Monsters", true);
		this.targetAnimalsSetting = new BooleanSetting("Target Animals", false);
		this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
		this.notHolding = new BooleanSetting("not Holding", false);
		this.aimSpeedSetting = new NumberSetting("AimSpeed", 0.45, 0.1, 1.0, 0.1);
		this.rangeSetting = new NumberSetting("Range", 5.0, 3.0, 8.0, 0.1);
		this.fov = new NumberSetting("FOV", 90.0D, 15.0D, 360.0D, 1.0D);
		sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","HurtTime","Distance", "Cycle"});
		ignoreBreaking = new BooleanSetting("Ignore Breaking", true);
		addSetting(notHolding, ignoreTeamsSetting, aimSpeedSetting,
			rangeSetting, targetAnimalsSetting, targetMonstersSetting, fov, sortmode,ignoreBreaking);
	}
	
	@Override
	public void onDisabled()
	{
		targets.clear();
		target = null;
	}
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventRender2D) {
			float tickDelta = mc.getTickDelta();
			setTag(sortmode.getValue()+" " + targets.size());
			collectTargets();
			if (!targets.isEmpty()) {
				target = targets.getFirst();
				if (e.isPost() || target == null || !canAssist()) {
					return;
				}
				if (mc.player == null)
					return;

				float diff = calculateYawChangeToDst(target);
				float aimSpeed = (float) aimSpeedSetting.value;
				aimSpeed = (float) MathHelper.clamp(
						RandomUtils.nextFloat(aimSpeed - 0.2f, aimSpeed + 1.8f),
						aimSpeedSetting.minimum, aimSpeedSetting.maximum);
				aimSpeed -= aimSpeed;

				if (diff < -6) {
					aimSpeed -= diff / 12f;
					mc.player.setYaw(mc.player.getYaw(tickDelta) - aimSpeed);
				} else if (diff > 6) {
					aimSpeed += diff / 12f;
					mc.player.setYaw(mc.player.getYaw(tickDelta) + aimSpeed);

				}
			}
		}
	}
	
	private boolean canAssist()
	{
		if(mc.currentScreen != null)
		{
			return false;
		}
		
		if(!notHolding.enabled && !mc.options.attackKey.isPressed())
		{
			return false;
		}
		if(ignoreBreaking.isEnabled() && mc.interactionManager.isBreakingBlock()){
			return false;
		}
		
		if(mc.player.isUsingItem())
		{
			return false;
		}
		
		return true;
	}
	
	private void collectTargets()
	{
		targets.clear();
		
		assert mc.world != null;
		for(Entity entity : mc.world.getEntities())
		{
			if(entity instanceof Entity && entity != mc.player)
			{
				if(!entity.isAlive() || entity.age < 10)
				{
					continue;
				}
				
				if(PlayerHelper.isInFov(entity, fov.value))
					continue;
				double focusRange =
					mc.player.canSee(entity) ? rangeSetting.value : 3.5;
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

		targets.sort(Comparator.comparingDouble(this::calculateYawChangeToDst));
		targets.sort(Comparator.comparingInt(o -> o.hurtTime));

	}

	private void sortTargets() {
		float yaw = Objects.requireNonNull(mc.player).getYaw();
		String Sort = sortmode.getMode();
		switch (Sort) {
			case "Distance":
				targets.sort((o1, o2) -> {
					double dist1 = Objects.requireNonNull(mc.player).squaredDistanceTo((Entity) o1);
					double dist2 = Objects.requireNonNull(mc.player).squaredDistanceTo((Entity) o2);
					return Double.compare(dist1, dist2);
				});
				break;
			case "Angle":
				targets.sort(Comparator.comparingDouble(RotationUtils::calculateYawChangeToDst));
				break;
			case "Cycle":
				targets.sort(Comparator.comparingDouble(player -> yawDistCycle(player, yaw)));
				break;
			case"HurtTime":
				targets.sort(Comparator.comparingInt(o -> o.hurtTime));
				break;
		}
	}

	private double yawDistCycle(LivingEntity e, float yaw) {
		Vec3d difference = e.getPos().add(0.0D, (e.getEyeHeight(e.getPose()) / 2.0F), 0.0D).subtract(Objects.requireNonNull(mc.player).getEyePos());
		return Math.abs(yaw - Math.atan2(difference.getZ(), difference.getX())) % 90.0D;
	}
	
	public float calculateYawChangeToDst(Entity entity)
	{
		double diffX = entity.getX() - Objects.requireNonNull(mc.player).getX();
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
}
