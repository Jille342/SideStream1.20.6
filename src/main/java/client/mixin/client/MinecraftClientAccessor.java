package client.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor
{
	
	@Mutable
	@Accessor("session")
	void setSession(Session session);
	
	@Invoker("doAttack")
	boolean accessDoAttack();
	
	@Invoker("doItemUse")
	void accessDoUseItem();
	
	@Accessor("renderTickCounter")
	RenderTickCounter getRenderTickCounter();
}
