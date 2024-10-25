package client.mixin.client;

import client.features.modules.ModuleManager;
import client.features.modules.combat.Reach;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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
	
}
