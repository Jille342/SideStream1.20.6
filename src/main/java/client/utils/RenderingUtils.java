package client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.util.BufferUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.NumberFormat;

import static me.x150.renderer.render.Renderer2d.renderQuad;
import static me.x150.renderer.util.RendererUtils.endRender;
import static me.x150.renderer.util.RendererUtils.setupRender;
import static org.lwjgl.opengl.GL11.*;

public class RenderingUtils implements MCUtil
{
	private static final Matrix4f model = new Matrix4f();
	private static final Matrix4f projection = new Matrix4f();

	public static void onRender(Matrix4f modelView)
	{
		model.set(modelView);
		projection.set(RenderSystem.getProjectionMatrix());
	}
	
	public static double interpolate(double current, double old, double scale)
	{
		return old + (current - old) * scale;
	}
	
	public static int[] getFractionIndicies(float[] fractions, float progress)
	{
		int[] range = new int[2];
		int startPoint = 0;
		while(startPoint < fractions.length
			&& fractions[startPoint] <= progress)
		{
			startPoint++;
		}
		
		if(startPoint >= fractions.length)
		{
			startPoint = fractions.length - 1;
		}
		
		range[0] = startPoint - 1;
		range[1] = startPoint;
		return range;
	}

	public static void drawRect(MatrixStack stack,double left, double top, double right,
								double bottom, int color)
	{
		renderQuad(stack, color, left, top, right, bottom);
	}


	public static void drawRect(DrawContext context, int color, double left,
		double top, double right, double bottom)
	{
		Color color2 = new Color(color);
		renderQuad(context.getMatrices(), color2, left, top, right, bottom);
	}
	
	public static void drawRect(MatrixStack stack, int color, double left,
		double top, double right, double bottom)
	{
		renderQuad(stack, color, left, top, right, bottom);
	}
	
	public static Color blendColors(float[] fractions, Color[] colors,
		float progress)
	{
		Color color = null;
		if(fractions.length == colors.length)
		{
			int[] indicies = getFractionIndicies(fractions, progress);
			if(indicies[0] < 0 || indicies[0] >= fractions.length
				|| indicies[1] < 0 || indicies[1] >= fractions.length)
			{
				return colors[0];
			}
			
			float[] range =
				new float[]{fractions[indicies[0]], fractions[indicies[1]]};
			Color[] colorRange =
				new Color[]{colors[indicies[0]], colors[indicies[1]]};
			float max = range[1] - range[0];
			float value = progress - range[0];
			float weight = value / max;
			color = blend(colorRange[0], colorRange[1], 1f - weight);
		}
		return color;
	}
	
	public static Color blend(Color color1, Color color2, double ratio)
	{
		float r = (float)ratio;
		float ir = (float)1.0 - r;
		
		float rgb1[] = new float[3];
		float rgb2[] = new float[3];
		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);
		
		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;
		
		if(red < 0)
		{
			red = 0;
		}else if(red > 255)
		{
			red = 255;
		}
		if(green < 0)
		{
			green = 0;
		}else if(green > 255)
		{
			green = 255;
		}
		if(blue < 0)
		{
			blue = 0;
		}else if(blue > 255)
		{
			blue = 255;
		}
		
		Color color = null;
		try
		{
			color = new Color(red, green, blue);
		}catch(IllegalArgumentException exp)
		{
			NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(nf.format(red) + "; " + nf.format(green) + "; "
				+ nf.format(blue));
			exp.printStackTrace();
		}
		return color;
	}
	
	public static void renderRect(MatrixStack matrices, double x1, double y1,
		double x2, double y2, int color)
	{
		double j;
		if(x1 < x2)
		{
			j = x1;
			x1 = x2;
			x2 = j;
		}
		
		if(y1 < y2)
		{
			j = y1;
			y1 = y2;
			y2 = j;
		}
		float f3 = (float)(color >> 24 & 255) / 255.0F;
		float f = (float)(color >> 16 & 255) / 255.0F;
		float f1 = (float)(color >> 8 & 255) / 255.0F;
		float f2 = (float)(color & 255) / 255.0F;
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(matrix, (float)x1, (float)y2, 0.0F).color(f, f1, f2, f3)
			.next();
		buffer.vertex(matrix, (float)x2, (float)y2, 0.0F).color(f, f1, f2, f3)
			.next();
		buffer.vertex(matrix, (float)x2, (float)y1, 0.0F).color(f, f1, f2, f3)
			.next();
		buffer.vertex(matrix, (float)x1, (float)y1, 0.0F).color(f, f1, f2, f3)
			.next();
		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		
		BufferUtils.draw(buffer);
		endRender();
	}

	
	public static void draw3DBox(MatrixStack matrixStack, Box box, int hex,
		float lineThickness)
	{
		float alpha = (hex >> 24 & 0xFF) / 255F;
		float red = (hex >> 16 & 0xFF) / 255F;
		float green = (hex >> 8 & 0xFF) / 255F;
		float blue = (hex & 0xFF) / 255F;
		RenderSystem.setShaderColor(red, green, blue, alpha);
		
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(red, green, blue, alpha);
		
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
			VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.maxZ);
		
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.minZ);
		
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.minZ);
		
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.maxZ);
		
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.minY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.maxX, (float)box.maxY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.maxZ);
		
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.minZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.minY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.maxZ);
		bufferBuilder.vertex(matrix4f, (float)box.minX, (float)box.maxY,
			(float)box.minZ);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(lineThickness);
		
		bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
			VertexFormats.POSITION);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.minY,
			(float)box.minZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.minY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.minY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.minY,
			(float)box.minZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.minY, (float)box.minZ, (float)box.minX, (float)box.maxY,
			(float)box.minZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY,
			(float)box.minZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.minY, (float)box.maxZ, (float)box.maxX, (float)box.maxY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.maxY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.maxY, (float)box.minZ, (float)box.maxX, (float)box.maxY,
			(float)box.minZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.maxY, (float)box.minZ, (float)box.maxX, (float)box.maxY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.maxX,
			(float)box.maxY, (float)box.maxZ, (float)box.minX, (float)box.maxY,
			(float)box.maxZ, hex);
		buildLine3d(matrixStack, bufferBuilder, (float)box.minX,
			(float)box.maxY, (float)box.maxZ, (float)box.minX, (float)box.maxY,
			(float)box.minZ, hex);
		
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		
		RenderSystem.enableCull();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
	
	public static void draw3DBox2(Matrix4f matrix4f, Box box, int color)
	{
		float alpha = (color >> 24 & 0xFF) / 255F;
		float red = (color >> 16 & 0xFF) / 255F;
		float green = (color >> 8 & 0xFF) / 255F;
		float blue = (color & 0xFF) / 255F;
		RenderSystem.setShaderColor(red, green, blue, 0.11F);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
			VertexFormats.POSITION);
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		
		tessellator.draw();
		
		RenderSystem.setShaderColor(red, green, blue, 0.8F);
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
			VertexFormats.POSITION);
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.maxX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.minZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.minY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.maxZ)
			.next();
		bufferBuilder
			.vertex(matrix4f, (float)box.minX, (float)box.maxY, (float)box.minZ)
			.next();
		tessellator.draw();


		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private static void buildLine3d(MatrixStack matrixStack,
		BufferBuilder bufferBuilder, float x1, float y1, float z1, float x2,
		float y2, float z2, int hex)
	{
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		
		Vec3d normalized = new Vec3d(x2 - x1, y2 - y1, z2 - z1).normalize();
		
		float alpha = (hex >> 24 & 0xFF) / 255F;
		float red = (hex >> 16 & 0xFF) / 255F;
		float green = (hex >> 8 & 0xFF) / 255F;
		float blue = (hex & 0xFF) / 255F;
		
		bufferBuilder.vertex(matrix4f, x1, y1, z1).color(red, green, blue, 1.0f)
			.normal(entry, (float)normalized.x, (float)normalized.y,
				(float)normalized.z);
		bufferBuilder.vertex(matrix4f, x2, y2, z2).color(red, green, blue, 1.0f)
			.normal(entry, (float)normalized.x, (float)normalized.y,
				(float)normalized.z);
	}



	
	public static float[] colorArray(final int hex)
	{
		float alpha = (hex >> 24 & 0xFF) / 255F;
		float red = (hex >> 16 & 0xFF) / 255F;
		float green = (hex >> 8 & 0xFF) / 255F;
		float blue = (hex & 0xFF) / 255F;
		return new float[]{
				red,green,blue,alpha
		};
	}

	public static void setAlphaLimit(float limit) {
		// Enable blending (transparency)
		RenderSystem.enableBlend();
		// Set blend function for transparency
		RenderSystem.defaultBlendFunc();

		// Set alpha function (not directly possible with RenderSystem)
		// Instead, we rely on blending setup, as OpenGL's alpha testing is deprecated
		float alphaThreshold = limit * 0.01f;
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alphaThreshold);
	}
}
