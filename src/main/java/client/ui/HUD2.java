package client.ui;

import client.features.modules.Module;
import client.features.modules.ModuleManager;

import client.features.modules.render.HUD;
import client.utils.Colors;
import client.utils.MathUtils;
import client.utils.RenderingUtils;
import client.utils.Translate;
import client.Client;
import client.utils.font.Fonts;
import client.utils.font.TTFFontRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import java.awt.Color;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static client.features.modules.render.HUD.namebackground;
import static client.features.modules.render.HUD.nameinfo;
import static client.utils.font.Fonts.titleFont;

public class HUD2
{
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public HUD2()
	{}
	
	public void draw(DrawContext context)
	{
		int[] counter = {1};
		int color = -1;
		switch(HUD.namecolormode.getMode())
		{
			case "Default":
			color = new Color(50, 200, 255).getRGB();
			break;
			case "Rainbow":
			color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
			break;
			case "Pulsing":
			color = TwoColoreffect(new Color(50, 200, 255), new Color(9, 9, 79),
				Math.abs(System.currentTimeMillis() / 10L) / 100.0
					+ 3.0F * (counter[0] * 2.55) / 60).getRGB();
			break;
			case "Test":
			color =
				TwoColoreffect(new Color(65, 179, 255), new Color(248, 54, 255),
					Math.abs(System.currentTimeMillis() / 10L) / 100.0
						+ 3.0F * (counter[0] * 2.55) / 60).getRGB();
			break;
		}
		Window scaledResolution = mc.getWindow();
		float height = 10;
		String name = Client.NAME;
		
		String build = "Build: \2477" + Client.VERSION;
		String blockps = "Blocks/s: \2477"
			+ String.valueOf(MathUtils.round(getDistTraveled(), 2));
		int nameX = 3;
		int nameY = 4;
		LocalTime localTime = LocalTime.now();
		String time = String.valueOf(localTime);
		// name = name.substring(0, 1).replaceAll(name.substring(0, 1), "\247c"
		// + name.substring(0, 1)) +
		// name.substring(1).replaceAll(name.substring(1), "\247f" +
		// name.substring(1));
		name = name.substring(0, 1).replaceAll(name.substring(0, 1),
			name.substring(0, 1))
			+ name.substring(1).replaceAll(name.substring(1),
				"\247f" + name.substring(1));
		if(nameinfo.enabled)
		{
			name = name + " " + String.format("[%s] [%dFPS]",
				new Object[]{time.substring(0, 5), mc.getCurrentFps()});
		}
		
		if(namebackground.isEnabled())
		{
			RenderingUtils.renderRect(context.getMatrices(), nameX, nameY,
				(int) titleFont.getStringWidth(name) + 5,
				(int) titleFont.getFontHeight() + 5,
				Colors.getColor(0, 0, 0, 50));
		}
		MatrixStack matrixStack = context.getMatrices();
		matrixStack.push();
		titleFont.drawString(matrixStack, name, nameX, nameY, color);
		matrixStack.pop();
		if(HUD.info.isEnabled())
		{
			Fonts.font.drawString(blockps, 3,
				scaledResolution.getScaledHeight() - height, -1);
		}
		Fonts.font.drawString(build, 5 , 16+titleFont.getFontHeight(), -1);
		
		if(HUD.armor.isEnabled())
		{
			drawArmorStatus(scaledResolution, context);
		}
		if(HUD.effects.isEnabled())
		{
			drawPotionStatus(scaledResolution);
		}
		this.drawGaeHud(context, scaledResolution);
	}
	
	private void drawGaeHud(DrawContext context, Window scaledResolution)
	{
		int width = scaledResolution.getScaledWidth();
		int height = scaledResolution.getScaledHeight();
		ArrayList<Module> sortedList = getSortedModules(Fonts.font);
		int listOffset = 10, y = 1;
		int[] counter = {1};
		
		for(int i = 0,
			sortedListSize = sortedList.size(); i < sortedListSize; i++)
		{
			Module module = sortedList.get(i);
			Translate translate = module.getTranslate();
			
			String moduleLabel = module.getDisplayName();
			float length = (float)Fonts.font.getStringWidth(moduleLabel);
			float featureX = width - length - 3.0F;
			boolean enable = module.isEnabled();
			if(enable)
			{
				translate.interpolate(featureX, y, 7);
			}else
			{
				translate.interpolate(width + 3, y, 7);
			}
			double translateX = translate.getX();
			double translateY = translate.getY();
			boolean visible = ((translateX > -listOffset));
			if(visible)
			{
				int color = -1;
				switch(HUD.colormode.getMode())
				{
					case "Default":
					color = new Color(50, 100, 255).getRGB();
					break;
					case "Rainbow":
					color = Colors.rainbow((counter[0] * 15) * 7, 0.8f, 1.0f);
					break;
					case "Pulsing":
					color = TwoColoreffect(new Color(50, 200, 255),
						new Color(9, 9, 79),
						Math.abs(System.currentTimeMillis() / 10L) / 100.0
							+ 3.0F * (counter[0] * 2.55) / 60).getRGB();
					break;
					case "Test":
					color = TwoColoreffect(new Color(65, 179, 255),
						new Color(248, 54, 255),
						Math.abs(System.currentTimeMillis() / 10L) / 100.0
							+ 3.0F * (counter[0] * 2.55) / 60).getRGB();
					break;
					case "Orange":
						color = TwoColoreffect(Color.orange,
								new Color(183, 131, 2),
								Math.abs(System.currentTimeMillis() / 10L) / 100.0
										+ 3.0F * (counter[0] * 2.55) / 60).getRGB();
						break;
				}
				MatrixStack matrixStack = context.getMatrices();
				int nextIndex = sortedList.indexOf(module) + 1;
				Module nextModule = null;
				if(sortedList.size() > nextIndex)
					nextModule = getNextEnabledModule(sortedList, nextIndex);
				
				if((Boolean)HUD.OUTLINE.enabled)
				{
					RenderingUtils.drawRect(context, translateX - 2.6D,
						translateY - 1.0D, translateX - 2.0D,
						translateY + listOffset - 1.0D, color);
					double offsetY = listOffset;
					if(nextModule != null)
					{
						double dif = (length - Fonts.font
							.getStringWidth(nextModule.getDisplayName()));
						RenderingUtils.drawRect(translateX - 2.6D,
							translateY + offsetY - 1.0D,
							translateX - 2.6D + dif,
							translateY + offsetY - 0.5D, color);
					}else
					{
						RenderingUtils.drawRect(translateX - 2.6D,
							translateY + offsetY - 1.0D, width,
							translateY + offsetY - 0.6D, color);
					}
				}
				if((Boolean)HUD.background.enabled)
					RenderingUtils.renderRect(context.getMatrices(),
						(int)(translateX - 2.0D), (int)(translateY - 1.0D),
						width, (int)(translateY + listOffset - 1.0D),
						Colors.getColor(0, 0, 0, 50));
				matrixStack.push();
				Fonts.font.drawString(matrixStack, moduleLabel,
					(float)translateX, (float)translateY, color);
				matrixStack.pop();
				if(module.isEnabled())
				{
					y += listOffset;
					counter[0] -= (int)1F;
				}
			}
		}
	}
	
	private Module getNextEnabledModule(ArrayList<Module> modules,
		int startingIndex)
	{
		for(int i = startingIndex,
			modulesSize = modules.size(); i < modulesSize; i++)
		{
			Module module = modules.get(i);
			if(module.isEnabled())
				return module;
		}
		return null;
	}
	
	private ArrayList<Module> getSortedModules(TTFFontRenderer fr)
	{
		ArrayList<Module> sortedList = new ArrayList<>(ModuleManager.modules);
		sortedList.sort(Comparator
			.comparingDouble(e -> -fr.getStringWidth(e.getDisplayName())));
		return sortedList;
	}
	
	public static Color TwoColoreffect(final Color color, final Color color2,
		double delay)
	{
		if(delay > 1.0)
		{
			final double n2 = delay % 1.0;
			delay = (((int)delay % 2 == 0) ? n2 : (1.0 - n2));
		}
		final double n3 = 1.0 - delay;
		return new Color((int)(color.getRed() * n3 + color2.getRed() * delay),
			(int)(color.getGreen() * n3 + color2.getGreen() * delay),
			(int)(color.getBlue() * n3 + color2.getBlue() * delay),
			(int)(color.getAlpha() * n3 + color2.getAlpha() * delay));
	}
	
	public static Color fade(Color color, int index, int count)
	{
		float[] hsb = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		float brightness =
			Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F
				+ index / count * 2.0F) % 2.0F - 1.0F);
		brightness = 0.5F + 0.5F * brightness;
		hsb[2] = brightness % 2.0F;
		return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
	}
	
	public double getDistTraveled()
	{
		double total = 0;
		for(double d : HUD.distances)
		{
			total += d;
		}
		return total;
	}
	
	private void drawPotionStatus(Window sr)
	{
		float pY = (mc.currentScreen != null) ? -26 : -12;
		assert mc.player != null;
		Collection<StatusEffectInstance> collection =
			mc.player.getStatusEffects();
		
		for(StatusEffectInstance effect : collection)
		{
			String name = I18n.translate(effect.getTranslationKey());
			String PType = "";
			if(effect.getAmplifier() == 1)
			{
				name = name + " II";
			}else if(effect.getAmplifier() == 2)
			{
				name = name + " III";
			}else if(effect.getAmplifier() == 3)
			{
				name = name + " IV";
			}
			if((effect.getDuration() < 600) && (effect.getDuration() > 300))
			{
				PType = PType + "\2476 " + effect.getDuration();
			}else if(effect.getDuration() < 300)
			{
				PType = PType + "\247c " + effect.getDuration();
			}else if(effect.getDuration() > 600)
			{
				PType = PType + "\2477 " + effect.getDuration();
			}
			Fonts.font.drawString(
				name, sr.getScaledWidth()
					- Fonts.font.getStringWidth(name + PType) - 3,
				sr.getScaledHeight() - 9 + pY, -1);
			Fonts.font.drawString(PType,
				sr.getScaledWidth() - Fonts.font.getStringWidth(PType) - 2,
				sr.getScaledHeight() - 9 + pY, -1);
			pY -= 9;
		}
	}
	
	private void drawArmorStatus(Window scaledRes, DrawContext context)
	{
		assert mc.player != null;
		MatrixStack matrixStack = context.getMatrices();
		if(!mc.player.isCreative())
		{
			int x = 15;
			matrixStack.push();
			for(int index = 3; index >= 0; index--)
			{
				ItemStack stack = mc.player.getInventory().armor.get(index);
				if(stack != null)
				{
					context.drawItem(stack,
						scaledRes.getScaledWidth() / 2 + x - 1,
						scaledRes.getScaledHeight()
							- (mc.player.isInsideWaterOrBubbleColumn() ? 70
								: 50)
							- 2);
					context.drawItem(stack,
						scaledRes.getScaledWidth() / 2 + x - 1,
						scaledRes.getScaledHeight()
							- (mc.player.isInsideWaterOrBubbleColumn() ? 70
								: 50)
							- 2);
					x += 18;
				}
			}
			matrixStack.pop();
		}
	}
	
}
