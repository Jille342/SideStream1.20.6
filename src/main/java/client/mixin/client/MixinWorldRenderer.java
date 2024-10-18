/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package client.mixin.client;

import client.Client;
import client.event.listeners.EventRender3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
	@Shadow
	private Frustum frustum;
	
	@Shadow
	private Vector3d capturedFrustumPosition;
	
	@Shadow
	@Nullable
	private Frustum capturedFrustum;
	
	@Inject(at = {@At("HEAD")},
		method = {
			"renderChunkDebugInfo(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/Camera;)V"},
		cancellable = true)
	private void onRenderChunkDebugInfo(MatrixStack matrices,
		VertexConsumerProvider vertexConsumers, Camera camera, CallbackInfo ci)
	{
		if(Client.moduleManager != null)
		{
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(2848);
			GL11.glEnable(2884);
			GL11.glDisable(2929);
			Frustum cameraFrustum = null;
			boolean bl = this.capturedFrustum != null;
			
			if(bl)
			{
				cameraFrustum = this.capturedFrustum;
				cameraFrustum.setPosition(this.capturedFrustumPosition.x,
					this.capturedFrustumPosition.y,
					this.capturedFrustumPosition.z);
			}else
			{
				cameraFrustum = this.frustum;
			}
			matrices.push();
			Vec3d camPos = MinecraftClient.getInstance()
				.getBlockEntityRenderDispatcher().camera.getPos();
			
			matrices.translate(-camPos.x, -camPos.y, -camPos.z);
			EventRender3D renderEvent = new EventRender3D(matrices,
				MinecraftClient.getInstance().getTickDelta(), cameraFrustum);
			Client.onEvent(renderEvent);
			matrices.pop();
			GL11.glEnable(2929);
			GL11.glDisable(3042);
			GL11.glDisable(2848);
		}
	}
	
	@Inject(at = @At("HEAD"),
		method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z",
		cancellable = true)
	private void onHasBlindnessOrDarknessEffect(Camera camera,
		CallbackInfoReturnable<Boolean> cir)
	{
		// TODO: NoRender
		// if (Aoba.getInstance().moduleManager.nooverlay.getState())
		// cir.setReturnValue(false);
	}
	
}
