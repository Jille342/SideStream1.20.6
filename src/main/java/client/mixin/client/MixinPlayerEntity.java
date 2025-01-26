package client.mixin.client;

import client.features.modules.ModuleManager;
import client.features.modules.combat.Reach;
import client.features.modules.movement.KeepSprint;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity
{
	
	@ModifyReturnValue(method = "getEntityInteractionRange", at = @At("RETURN"))
	private double hookEntityInteractionRange(double original)
	{
		if((Object)this == MinecraftClient.getInstance().player)
		{
			if(ModuleManager.getModulebyClass(Reach.class).isEnabled())
			{
				return Reach.reach.getValue();
			}
			
		}
		return original;
	}
	@WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
	private boolean keepSprintHook(PlayerEntity instance, Vec3d vec3d) {
		return ModuleManager.getModulebyClass(KeepSprint.class).isEnabled();
	}

	@WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", ordinal = 0))
	private boolean keepSprintHook(PlayerEntity instance, boolean b) {
		return  ModuleManager.getModulebyClass(KeepSprint.class).isEnabled();
	}

	
}
