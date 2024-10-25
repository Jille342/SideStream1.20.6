/*
 * Copyright (c) 2022 Coffee Client, 0x150 and contributors.
 * Some rights reserved, refer to LICENSE file.
 */

package client.mixin.client;

import client.features.modules.ModuleManager;
import client.features.modules.player.NoBreakDelay;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager
{
	@Shadow
	private int blockBreakingCooldown;
	
	@Redirect(method = "updateBlockBreakingProgress",
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
			opcode = Opcodes.GETFIELD,
			ordinal = 0))
	public int coffee_overwriteCooldown(
		ClientPlayerInteractionManager clientPlayerInteractionManager)
	{
		int cd = this.blockBreakingCooldown;
		return Objects
			.requireNonNull(ModuleManager.getModulebyClass(NoBreakDelay.class))
			.isEnabled() ? 0 : cd;
	}
}
