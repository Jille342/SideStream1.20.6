package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventRender3D;
import client.features.modules.Module;
import client.settings.ModeSetting;
import client.utils.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;

public class EntityESP extends Module
{
	static ModeSetting mode;
	public ModeSetting colorMode;
	
	public EntityESP()
	{
		
		super("EntityESP", 0, Category.RENDER);
		
	}
	
	@Override
	public void init()
	{
		super.init();
		colorMode = new ModeSetting("Color Mode", "HurtTime",
			new String[]{"HurtTime", "Team"});
		
		mode = new ModeSetting("Mode ", "BoundingBox",
			new String[]{"BoundingBox"});
		addSetting(mode, colorMode);
	}
	
	@Override
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventRender3D)
		{
			MatrixStack matrixStack = ((EventRender3D)event).getMatrix();
			float partialTicks = ((EventRender3D)event).getPartialTicks();
			
			for(AbstractClientPlayerEntity entity : Objects
				.requireNonNull(mc.world).getPlayers())
			{
				
				Camera camera = mc.gameRenderer.getCamera();
				Vec3d cameraPosition = camera.getPos();
				if(mc.getEntityRenderDispatcher().shouldRender(entity,
					((EventRender3D)event).getFrustum(), cameraPosition.getX(),
					cameraPosition.getY(), cameraPosition.getZ()))
				{
					if((entity != null) && entity != mc.player)
					{
						int color = 0;
						
						if(colorMode.getMode().equalsIgnoreCase("Team"))
						{
							color =
								(ServerHelper.isTeammate((PlayerEntity)entity))
									? Colors.getColor(60, 255, 60)
									: Colors.getColor(255, 60, 60);
						}else if(colorMode.getMode()
							.equalsIgnoreCase("HurtTime"))
						{
							color = (entity.hurtTime == 0)
								? new Color(0, 200, 0, 100).getRGB()
								: new Color(239, 235, 41, 255).getRGB();
						}
						
						switch(mode.getMode())
						{
							case "BoundingBox":
							double interpolatedX = MathHelper.lerp(partialTicks,
								entity.prevX, entity.getX());
							double interpolatedY = MathHelper.lerp(partialTicks,
								entity.prevY, entity.getY());
							double interpolatedZ = MathHelper.lerp(partialTicks,
								entity.prevZ, entity.getZ());
							
							Box boundingBox = entity.getBoundingBox().offset(
								interpolatedX - entity.getX(),
								interpolatedY - entity.getY(),
								interpolatedZ - entity.getZ());
							RenderingUtils.draw3DBox2(
								matrixStack.peek().getPositionMatrix(),
								boundingBox, color);
							break;
							case "Lines":
							// RenderingUtils.drawEntityModel(matrixStack,
							// partialTicks, entity, color,
							// lineThickness.getValue());
							break;
						}
					}
				}
			}
		}
	}
}
