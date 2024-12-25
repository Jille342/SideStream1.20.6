package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventMove;
import client.event.listeners.EventUpdate;
import client.event.listeners.EventUpdateVelocity;
import client.features.modules.Module;
import client.features.modules.combat.AntiVelocity;
import client.features.modules.combat.LegitAura2;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.MCTimerUtil;
import client.utils.MoveUtils;
import client.utils.RotationUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugSpeed extends Module {

	NumberSetting timerSpeed;
	ModeSetting modeSetting;
	NumberSetting jumpSpeed;
	NumberSetting fallSpeed;
	NumberSetting matrixDecrease;
	private float yaw;
	private boolean direction;
	private int jumpCount;
	private  BooleanSetting targetStrafe;
	private  BooleanSetting targetStrafeWhileSpace;




	public DebugSpeed() {
		super("DebugSpeed", 0, Category.MOVEMENT);
	}

	@Override
	public void init() {
		super.init();
		timerSpeed = new NumberSetting("Timer Speed", 1, 1, 5, 0.01F);
		modeSetting = new ModeSetting("Mode", "Timer", "Timer", "Bhop", "Matrix");
		fallSpeed = new NumberSetting("Fall Speed", 0.22, 0.01, 0.3, 0.01);
		jumpSpeed = new NumberSetting("Jump Speed", 0.33, 0.01, 0.5, 0.01);
		matrixDecrease = new NumberSetting("Matrix Decrease", 0.01, 0, 0.2, 0.01);
		targetStrafe = new BooleanSetting("Target Strafe", true);
		targetStrafeWhileSpace = new BooleanSetting("Target Strafe While Space" ,true);
		addSetting(targetStrafeWhileSpace,targetStrafe,timerSpeed, modeSetting, fallSpeed, jumpSpeed, matrixDecrease);
	}

	public void onEnabled() {
		this.yaw = mc.player.getYaw();

		super.onEnabled();
	}

	public void onDisabled() {
		mc.options.jumpKey.setPressed(false);

		MCTimerUtil.setTimerSpeed(1);
		super.onDisabled();
	}

	public void onEvent(Event<?> event) {
		if(event instanceof EventUpdateVelocity){
			if (!mc.player.isTouchingWater()) {
				switch (this.modeSetting.getValue()) {
					case "Matrix":
						if (!MoveUtils.isMoving()) {
							return;
						} else {
							((EventUpdateVelocity) event).yaw = this.yaw + 0.1F;
						}
				}
			}

		}
		if (event instanceof EventUpdate) {
			setTag(modeSetting.getValue());
			if (mc.player.horizontalCollision) {
				this.direction = !this.direction;
			}

			if (modeSetting.getMode().equalsIgnoreCase("Matrix")) {

				if (!MoveUtils.isMoving()) {
					this.jumpCount = 0;
					return;
				}

				mc.player.setSprinting(true);
				double yaw = MoveUtils.getDirection(RotationUtils.virtualYaw);
				MCTimerUtil.setTimerSpeed(1.0F);
				double radius = 4.0;
				float speed;
				if (LegitAura2.target != null && this.targetStrafe.getValue() && (!this.targetStrafeWhileSpace.getValue() || mc.options.jumpKey.isPressed())) {
					MCTimerUtil.setTimerSpeed((float) this.timerSpeed.getValue());
					yaw = (double) RotationUtils.rotation(LegitAura2.target.getPos(), mc.player.getPos())[0];
					if (!(mc.player.getPos().distanceTo(LegitAura2.target.getPos()) > 4.0)) {
						speed = (float) (System.currentTimeMillis() - AntiVelocity.lastVelocity > 3000L ? 70 : 90);
						if (this.direction) {
							yaw += (double) speed;
						} else {
							yaw -= (double) speed;
						}
					}

					yaw = Math.toRadians(yaw);
				}

				this.yaw = RotationUtils.rotation(mc.player.getPos().add(-Math.sin((double) ((float) yaw)), mc.player.getVelocity().y, Math.cos((double) ((float) yaw))), mc.player.getPos())[0];
				if (mc.player.isOnGround()) {
					++this.jumpCount;
					speed = (float) MoveUtils.getSpeed();
					mc.player.setVelocity(-Math.sin((double) ((float) yaw)) * (double) speed, mc.player.getVelocity().y, Math.cos((double) ((float) yaw)) * (double) speed);
					if (!mc.options.jumpKey.isPressed()) {
						mc.player.jump();
					}

					speed = (float) MoveUtils.getSpeed();
					mc.player.setVelocity(-Math.sin((double) ((float) yaw)) * (double) speed, mc.player.getVelocity().y, Math.cos((double) ((float) yaw)) * (double) speed);
				}

				if (System.currentTimeMillis() - AntiVelocity.lastVelocity < 1500L) {
					if (mc.player.isOnGround()) {
						AntiVelocity.lastVelocity = System.currentTimeMillis() - 1500L;
					}

					speed = (float) MoveUtils.getSpeed() * 0.99999F;
					mc.player.setVelocity(-Math.sin((double) ((float) yaw)) * (double) speed, mc.player.getVelocity().y + 0.0034, Math.cos((double) ((float) yaw)) * (double) speed);
				} else {
					mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y - 0.0034, mc.player.getVelocity().z);
				}
			} else
if(modeSetting.getMode().equalsIgnoreCase("Timer")){
	// Loop to check surrounding blocks
	for (int x = -2; x <= 2; ++x) {
		for (int y = -2; y <= 2; ++y) {
			for (int z = -2; z <= 2; ++z) {
				if (mc.world.getBlockState(mc.player.getBlockPos().add(x, y, z)).getBlock() == Blocks.REDSTONE_BLOCK) {
					mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(mc.player.getBlockPos().toCenterPos(), Direction.UP, mc.player.getBlockPos().add(x, y, z), false));
					mc.player.swingHand(Hand.MAIN_HAND);
				}
			}
		}
	}

	// Speed management
	if (mc.interactionManager.isBreakingBlock()) {
		MCTimerUtil.setTimerSpeed(1.0f);
	} else {
		MCTimerUtil.setTimerSpeed((float) timerSpeed.getValue());
		if (!mc.player.isOnGround() && mc.player.isSprinting()) {
			mc.player.setSprinting(true);
			mc.player.input.pressingForward = true;
		} else {
			mc.player.setSprinting(false);
			mc.player.input.pressingForward = false;
			if (!mc.player.isClimbing() && !mc.player.isSubmergedInWater()) {
				if (mc.player.isHoldingOntoLadder()) {
					mc.player.setVelocity(mc.player.getVelocity().multiply(0.6, 1.0, 0.6));
				} else {

					float speedScale = 0.7f;
					if (mc.player.getActiveStatusEffects().entrySet().stream()
							.anyMatch(entry -> entry.getKey() == StatusEffects.SLOWNESS)) {
						AtomicInteger level = new AtomicInteger();
						mc.player.getActiveStatusEffects().entrySet().stream()
								.filter(entry -> entry.getKey() == StatusEffects.SLOWNESS)
								.map(Map.Entry::getValue) // Get the StatusEffectInstance
								.findFirst()
								.ifPresent(effect -> {
								level.set(effect.getAmplifier());
									// Now you have the level of the Slowness effect
								});
						if (level.get() == 2) {
							speedScale = 0.5f;
						}
					}

					mc.player.setVelocity(mc.player.getVelocity().multiply(speedScale, 1.0, speedScale));
				}
			}
		}
	}}

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
			}
		}
	}

}
