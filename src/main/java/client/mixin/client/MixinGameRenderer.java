package client.mixin.client;

import client.Client;
import client.event.listeners.EventRenderWorld;
import client.features.modules.ModuleManager;
import client.features.modules.render.NameTags;
import client.features.modules.render.NoHurtcam;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
	@Shadow
	@Final
	private Camera camera;
	MinecraftClient mc = MinecraftClient.getInstance();
	
	/**
	 * Hook world render event
	 */
	@Inject(method = "renderWorld",
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		locals = LocalCapture.CAPTURE_FAILHARD)
	public void onRenderWorld(float tickDelta, long limitTime, CallbackInfo ci,
		boolean bl, Camera camera, Entity entity, double d, Matrix4f matrix4f,
		MatrixStack matrixStack, float f, float g, Matrix4f matrix4f2)
	{
		EventRenderWorld event = new EventRenderWorld(tickDelta, matrixStack);
		Client.onEvent(event);
		
	}
	
	@Inject(at = @At("HEAD"),
		method = "tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		cancellable = true)
	private void onTiltViewWhenHurt(MatrixStack matrices, float tickDelta,
		CallbackInfo ci)
	{
		if(ModuleManager.getModulebyClass(NoHurtcam.class).isEnabled())
			ci.cancel();
	}
	
	@Inject(
		at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
			opcode = Opcodes.GETFIELD,
			ordinal = 0),
		method = "renderWorld")
	void render3dHook(float tickDelta, long limitTime, CallbackInfo ci)
	{
		Camera camera = mc.gameRenderer.getCamera();
		MatrixStack matrixStack = new MatrixStack();
		RenderSystem.getModelViewStack().pushMatrix()
			.mul(matrixStack.peek().getPositionMatrix());
		matrixStack.multiply(
			RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrixStack.multiply(
			RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
		RenderSystem.applyModelViewMatrix();
		
		NameTags.lastProjMat.set(RenderSystem.getProjectionMatrix());
		NameTags.lastModMat.set(RenderSystem.getModelViewMatrix());
		NameTags.lastWorldSpaceMatrix
			.set(matrixStack.peek().getPositionMatrix());
		
		RenderSystem.getModelViewStack().popMatrix();
		RenderSystem.applyModelViewMatrix();
	}
	
}
