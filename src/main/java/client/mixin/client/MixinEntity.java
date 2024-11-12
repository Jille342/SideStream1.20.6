
package client.mixin.client;

import client.Client;
import client.event.listeners.EventMove;
import client.features.modules.ModuleManager;
import client.features.modules.combat.HitBoxes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class MixinEntity
{
	@Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
	private void onGetTargetingMargin(CallbackInfoReturnable<Float> cir)
	{
		if(ModuleManager.getModulebyClass(HitBoxes.class).isEnabled())
		{
			cir.setReturnValue(HitBoxes.getSize((Entity)(Object)this));
		}
	}
}
