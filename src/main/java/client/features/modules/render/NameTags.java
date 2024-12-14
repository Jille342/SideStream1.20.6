package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventNameTag;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventRender3D;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.NumberSetting;
import client.utils.RenderingUtils;
import client.utils.font.TTFFontRenderer;
import java.awt.Color;
import java.util.Comparator;
import java.util.Objects;

import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import client.utils.TickManager;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;

public class NameTags extends Module
{
	public static final Matrix4f lastProjMat = new Matrix4f();
	public static final Matrix4f lastModMat = new Matrix4f();
	public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
	private static final int MAX_SAMPLES =
		GL30.glGetInteger(GL30C.GL_MAX_SAMPLES);
	private final TTFFontRenderer font = TTFFontRenderer.of("ElliotSans", 8);
	private final TTFFontRenderer nameDrawer = TTFFontRenderer.of("ElliotSans", 12);
		public static NumberSetting size;

		public BooleanSetting armor;
	
	public NameTags()
	{
		super("NameTags", 0, Category.RENDER);
		size = new NumberSetting("Size", 1.0, 1.0, 5.0,0.1);
		armor = new BooleanSetting("Armor", true);
		addSetting(size,armor);
	}

	
	public void onEvent(Event<?> event)
	{
		if(event instanceof EventRender2D)
		{
			;
			if(mc.player == null || mc.world == null)
				return;
			if(mc.gameRenderer.getCamera() == null)
				return;
			MSAAFramebuffer.use(Math.min(16, MAX_SAMPLES), () -> {
				for(final AbstractClientPlayerEntity player : mc.world
					.getPlayers().stream()
					.sorted(Comparator.comparingDouble(value -> -value.getPos()
						.distanceTo(mc.gameRenderer.getCamera().getPos())))
					.filter(
						abstractClientPlayerEntity -> !abstractClientPlayerEntity
							.equals(mc.player))
					.toList())
				{
					this.render(((EventRender2D) event), player,
						player.getName());
				}
			});
		}
		
	}
	
	public void render(EventRender2D event,
		final AbstractClientPlayerEntity entity, final Text text)
	{
		MatrixStack stack = event.getContext().getMatrices();
		final String t = text.getString();
		
		final Vec3d headPos = getInterpolatedEntityPosition(entity).add(0,
			entity.getHeight() + 0.3, 0);
		final Vec3d a = getScreenSpaceCoordinate(headPos, stack);
		if(isOnScreen(a))
		{
		//	TickManager.runOnNextRender(
		//		(event) -> this.drawInternal(event, a, t, entity));
			drawInternal(event, a,t, entity);
		}
	}
	
	public static Vec3d getInterpolatedEntityPosition(final Entity entity)
	{
		final Vec3d a = entity.getPos();
		final Vec3d b = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
		final float p = mc.getTickDelta();
		return new Vec3d(MathHelper.lerp(p, b.x, a.x),
			MathHelper.lerp(p, b.y, a.y), MathHelper.lerp(p, b.z, a.z));
	}
	
	public static Vec3d getScreenSpaceCoordinate(final Vec3d pos,
		final MatrixStack stack)
	{
		final Camera camera = mc.getEntityRenderDispatcher().camera;
		if(camera == null)
			return null;
		final int displayHeight = mc.getWindow().getHeight();
		final int[] viewport = new int[4];
		final Vector3f target = new Vector3f();
        double deltaX = pos.x - camera.getPos().x;
		 double deltaY = pos.y - camera.getPos().y;
		 double deltaZ = pos.z - camera.getPos().z;

		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		
		final Vector4f transformedCoordinates =
			new Vector4f((float)deltaX, (float)deltaY, (float)deltaZ, 1.f)
				.mul(lastWorldSpaceMatrix);

        lastProjMat.mul(lastModMat).project(transformedCoordinates.x(),
			transformedCoordinates.y(), transformedCoordinates.z(), viewport,
			target);
		
		return new Vec3d(target.x / mc.getWindow().getScaleFactor(),
			(displayHeight - target.y) / mc.getWindow().getScaleFactor(),
			target.z);
	}
	
	public static boolean isOnScreen(final Vec3d pos)
	{
		return pos != null && pos.z > -1 && pos.z < 1;
	}
	
	void drawInternal(final EventRender2D eve, final Vec3d screenPos,
		final String text, final AbstractClientPlayerEntity entity) {

		final double labelHeight = 2 + nameDrawer.getFontHeight();
		int ping = -1;
		final PlayerListEntry ple = Objects.requireNonNull(mc.getNetworkHandler()).getPlayerListEntry(entity.getUuid());
		if (ple != null) {
			ping = ple.getLatency();
		}

		final String pingStr = (ping == 0 ? "?" : ping) + " ms";
		final MatrixStack stack1 = eve.getContext().getMatrices();
		stack1.push();
		final Vec3d actual =
				new Vec3d(screenPos.x, screenPos.y - labelHeight, screenPos.z);
		float width = nameDrawer.getStringWidth(text) + 4;
		Color color = (entity.isSneaking() || entity.isInvisible())
				? new Color(100, 0, 0, 100) : new Color(0, 0, 5, 100);

	RenderingUtils.renderRect(stack1, actual.x - width / 2d, actual.y, actual.x + width / 2d, actual.y + labelHeight, color.getRGB());
		nameDrawer.drawString(stack1, text,
				actual.x + width / 2d - nameDrawer.getStringWidth(text) - 2,
				actual.y + 2, entity.getTeamColorValue());
		if (ping != -1) {
			this.font.drawString(stack1, pingStr, actual.x - width / 2d + 2,
					actual.y + 2 + nameDrawer.getFontHeight(), 0xAAAAAA);
		}
		if (armor.getValue()) {
			float xOffset = 0f;
			for (final ItemStack stack : entity.getInventory().armor) {
				if (stack.getItem() != Items.AIR) {
					eve.getContext().drawItem(stack,
							(int) (actual.x - width / 2d + 2
									+ font.getStringWidth(pingStr) + xOffset),
							(int) (actual.y - 10));
				}
				xOffset += 20;
			}

			eve.getContext().drawItem(entity.getInventory().getMainHandStack(),
					(int) (actual.x - width / 2d + 2 - 10), (int) (actual.y - 10));
		}

		stack1.pop();
	}
	/*

	public void onNameTag(EventNameTag eventNameTag){
		eventNameTag.cancel();
	}

	 */
}
