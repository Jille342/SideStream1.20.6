package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventMove;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.MCTimerUtil;
import client.utils.MoveUtils;
import client.utils.PlayerHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Objects;

public class DebugSpeed extends Module
{
	
	NumberSetting timerSpeed;
	ModeSetting modeSetting;
	NumberSetting jumpSpeed;
	NumberSetting fallSpeed;
	NumberSetting matrixDecrease;



	public DebugSpeed()
	{
		super("DebugSpeed", 0, Category.MOVEMENT);
	}
	
	@Override
	public void init()
	{
		super.init();
		timerSpeed = new NumberSetting("Timer Speed", 1, 1, 5, 0.01F);
		modeSetting = new ModeSetting("Mode", "Timer", "Timer","Bhop","Matrix");
		fallSpeed = new NumberSetting("Fall Speed", 0.22, 0.01, 0.3, 0.01);
		jumpSpeed = new NumberSetting("Jump Speed", 0.33, 0.01, 0.5, 0.01);
matrixDecrease = new NumberSetting("Matrix Decrease",0.01, 0, 0.2, 0.01);
		addSetting(timerSpeed,modeSetting, fallSpeed, jumpSpeed, matrixDecrease);
	}
	
	public void onEnabled()
	{

		//MCTimerUtil.setTimerSpeed((float)this.timerSpeed.getValue());

		super.onEnabled();
	}
	
	public void onDisabled()
	{
		mc.options.jumpKey.setPressed(false);

		MCTimerUtil.setTimerSpeed(1);
		super.onDisabled();
	}
	
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventUpdate) {
			setTag(modeSetting.getValue());
			/*
			if (modeSetting.getMode().equalsIgnoreCase("Timer")) {
				if (Objects.requireNonNull(mc.interactionManager).isBreakingBlock()) {
					MCTimerUtil.setTimerSpeed(1f);
					return;
				} else {
					MCTimerUtil.setTimerSpeed((float) this.timerSpeed.getValue());
				}
				if (Objects.requireNonNull(mc.player).isTouchingWater()) {
					mc.player.setSprinting(true);
					mc.options.sprintKey.setPressed(true);
					mc.player.setSwimming(true);
					return;
				} else {
					mc.player.setSprinting(false);
					mc.options.sprintKey.setPressed(false);
					mc.player.setSwimming(false);
				}
				if (mc.player.isSneaking() || mc.player.isUsingItem())
					return;
				if (mc.player.isSprinting()) {
					mc.player.setVelocity(mc.player.getVelocity().x * 0.6,
							mc.player.getVelocity().y, mc.player.getVelocity().z * 0.6);
				} else {
					float scala = 0.7f;
					if (mc.player.getStatusEffects().stream().toList().stream()
							.anyMatch(p -> p.getEffectType() == StatusEffects.SPEED)) {
						final int amp = mc.player.getStatusEffects().stream()
								.toList().stream()
								.filter(p -> p.getEffectType() == StatusEffects.SPEED)
								.findAny().get().getAmplifier();
						if (amp == 2) {
							scala = 0.5f;
						}
					}
					mc.player.setVelocity(mc.player.getVelocity().x * scala,
							mc.player.getVelocity().y,
							mc.player.getVelocity().z * scala);
				}
			}

			 */
		}
		if(event instanceof EventMove){
			if(modeSetting.getMode().equalsIgnoreCase("Bhop")) {
				double moveSpeed = MoveUtils.getBaseMoveSpeed();
				if ((mc.player.input.movementForward !=0 || mc.player.input.movementSideways != 0) && mc.player.isOnGround()) {
		//	((EventMove) event).setY(getHop(0.42));
		//		mc.player.jump();
mc.options.jumpKey.setPressed(true);
				MoveUtils.setMotion(((EventMove) event),moveSpeed );
				} else {
					mc.options.jumpKey.setPressed(false);
				}

				if (mc.player.fallDistance > 0.2) {
					if(mc.player.input.movementForward!=0) {
						MoveUtils.setMotion(((EventMove) event), moveSpeed);
					} else if(mc.player.input.movementSideways != 0){
						MoveUtils.setMotion(((EventMove) event), moveSpeed - matrixDecrease.getValue());
					}
				}
			}else if(modeSetting.getMode().equalsIgnoreCase("Matrix")){
				double moveSpeed = MoveUtils.getBaseMoveSpeed();
				EventMove eventMove = (EventMove) event;
					if (mc.player.input.movementForward== 0.0f && mc.player.input.movementSideways == 0.0f) {
						moveSpeed = MoveUtils.getBaseMoveSpeed() - matrixDecrease.getValue();  // Reset speed if no input
					}

					// Gradual speed increase with cap
					if (MoveUtils.isMoving()) {
						moveSpeed += 0.01;  // Slowly increase speed
						moveSpeed = Math.min(moveSpeed, MoveUtils.getBaseMoveSpeed());  // Cap at 10% boost
					} else {
					moveSpeed = MoveUtils.getBaseMoveSpeed();  // Reset when stopping
					}

					// Handle safe jump height
					if (mc.player.isOnGround()) {
						double motY = 0.42;  // Safe jump height
						eventMove.setY(motY);
						mc.player.jump();
					}

					// Apply minor speed boost on damage if within allowed limits
					if (mc.player.hurtTime > 0 && moveSpeed < MoveUtils.getBaseMoveSpeed() * 1.15) {
						moveSpeed *= 1.05;  // Minor boost on damage
					}

					// Apply movement with limit on strafing

				MoveUtils.setMotion(eventMove, moveSpeed);

			}
		}
	}
	protected double getHop(double height) {
		StatusEffectInstance jumpBoost = mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) ? mc.player.getStatusEffect(StatusEffects.JUMP_BOOST) : null;
		if (jumpBoost != null) height += (jumpBoost.getAmplifier() + 1) * 0.1f;
		return height;
	}
}
