
package client.mixin.client;

import client.features.modules.ModuleManager;
import client.features.modules.combat.HitBoxes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class MixinEntity
{
	@Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
	private void onGetTargetingMargin(CallbackInfoReturnable<Float> cir)
	{
		if(ModuleManager.getModulebyClass(HitBoxes.class).isEnable())
		{
			cir.setReturnValue(HitBoxes.getSize((Entity)(Object)this));
		}
	}
}
