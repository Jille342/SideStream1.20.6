package client.ui.clicckgui;

import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.settings.*;

import client.utils.Colors;
import client.utils.font.Fonts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static client.utils.RenderingUtils.drawRect;
import static client.utils.RenderingUtils.renderRect;

public class ClickGuiWindow
{
	
	MinecraftClient mc = MinecraftClient.getInstance();
	
	private NumberSetting doubleSetting;
	public Module module;
	private static final int defaultColor = new Color(0, 200, 255,158).getRGB();
	private static final Color backColor = new Color(0x67373737, true);
	private static final int outlineColor1 = Colors.getColor(0, 0, 0, 50);
	private static final Color outlineColor2 = new Color(0xff313131);
	private static final int settingTextColor = 0xffd0d0d0;
	
	private float x, y, lastX, lastY;
	private boolean dragging = false, expand = true;
	
	private final Module.Category category;
	private final List<Module> modules;
	private final boolean[] mExpand;
	int keyCode;
	KeyBindSetting keyBindSetting = null;
	private static boolean clicked = false;
	public int width;
	public int height;
	
	public ClickGuiWindow(float x, float y, Module.Category category)
	{
		this.x = x;
		this.y = y;
		this.category = category;
		modules = ModuleManager.modules.stream()
			.filter(m -> m.getCategory() == category)
			.collect(Collectors.toList());
		mExpand = new boolean[modules.size()];
	}
	
	public void init()
	{
		
	}
	
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void render(MatrixStack stack, int mouseX, int mouseY, float delta)
	{
		if(doubleSetting != null)
		{
			doubleSetting.setValue(x, 120, mouseX);
		}
		
		if(dragging)
		{
			x = mouseX + lastX;
			y = mouseY + lastY;
		}
		renderRect(stack,x - 2, y - 2, x + 122, y + 20, defaultColor);
		renderRect(stack,x - 1, y - 1, x + 121, y + 19, outlineColor1);
		renderRect(stack,x, y, x + 120, y + 18, 0xff262626);
		renderRect(stack,  x - 1, y + 17, x + 121, y + 18,outlineColor1);
		Fonts.font.drawString(stack,category.name(), x + 4, y + 4, -1);
		
		if(!expand)
		{
			return;
		}
		
		float currentY = y + 18;
		for(int i = 0; i < modules.size(); i++)
		{
			Module m = modules.get(i);
			drawRect(stack, defaultColor, x - 2, currentY, x + 122,
				currentY + 20);
			drawRect(stack, outlineColor1, x - 1, currentY, x + 121,
				currentY + 19);
			drawRect(stack,
				m.isEnabled() ? defaultColor : backColor.getRGB(), x,
				currentY, x + 120, currentY + 18);
			if(m.getKeyCode() == 0
				|| m.getKeyCode() == GLFW.GLFW_KEY_RIGHT_SHIFT
				|| (GLFW.glfwGetKeyName(m.getKeyCode(), 1)) == null)
			{
				Fonts.font.drawString(stack,m.getName(),
					x + 116 - Fonts.font.getStringWidth(m.getName()),
					currentY + 4, -1);
			}else if(((GLFW.glfwGetKeyName(m.getKeyCode(), 1)) != null))
			{
				String displayKeyCode = String.format("%s [%s]", m.getName(),
					Objects.requireNonNull(GLFW.glfwGetKeyName(m.getKeyCode(), 1)).toUpperCase());
				Fonts.font.drawString(stack,displayKeyCode,
					x + 116 - Fonts.font.getStringWidth(displayKeyCode),
					currentY + 4, -1);
			}
			
			currentY += 18;
			
			if(!mExpand[i])
			{
				continue;
			}
			
			for(int j = 0; j < m.settings.size(); j++)
			{
				final Setting<?> s = m.settings.get(j);
				drawRect(stack, defaultColor, x - 2, currentY,
					x + 122, currentY + 20);
				drawRect(stack, defaultColor, x - 2, currentY,
					x + 122, currentY + 20);
				drawRect(stack, outlineColor1, x - 1, currentY,
					x + 121, currentY + 19);
				if(s instanceof NumberSetting ds)
				{
                    // final String v = String.valueOf(ds.getValue());
					final String v =
						String.valueOf((Math.floor(ds.getValue() * 100)) / 100);
					drawRect(stack, outlineColor2.getRGB(), x, currentY,
						x + 120, currentY + 18);
					drawRect(stack, defaultColor, x, currentY + 2,
						(float)(x + ds.getPercentage() * 120), currentY + 16);
					Fonts.font.drawString(stack,s.name, x + 4, currentY + 4,
						settingTextColor);
					Fonts.font.drawString(stack,v,
						x + 116 - Fonts.font.getStringWidth(v), currentY + 4,
						-1);
				}else if(s instanceof ModeSetting ms)
				{
                    drawRect(stack, outlineColor2.getRGB(), x, currentY,
						x + 120, currentY + 18);
					Fonts.font.drawString(stack,ms.name, x + 4,
						currentY + 4, settingTextColor);
					Fonts.font.drawString(stack,ms.getMode(),
						x + 116 - Fonts.font.getStringWidth(ms.getMode()),
						currentY + 4, -1);
					if(ms.expand)
					{
						for(String o : ms.modes)
						{
							currentY += 18;
							drawRect(stack, defaultColor, x - 2,
								currentY, x + 122, currentY + 20);
							drawRect(stack, outlineColor1, x - 1,
								currentY, x + 121, currentY + 19);
							drawRect(stack, outlineColor2.getRGB(), x,
								currentY, x + 120, currentY + 18);
							Fonts.font.drawString(stack,o, x + 4, currentY + 4,
								settingTextColor);
							// currentY += 18;
						}
					}
				}else if(s instanceof BooleanSetting bs)
				{
                    drawRect(stack,
						bs.isEnabled() ? defaultColor
							: outlineColor2.getRGB(),
						x, currentY, x + 120, currentY + 18);
					Fonts.font.drawString(stack,s.name, x + 4, currentY + 4,
						settingTextColor);
				}else if(s instanceof KeyBindSetting setting)
				{
					drawRect(stack, outlineColor2.getRGB(), x, currentY,
							x + 120, currentY + 18);
                    if(GLFW.glfwGetKeyName(setting.getKeyCode(), 1) != null) {
						Fonts.font
								.drawString(stack,
										setting.name + ": "
												+ (clicked ? "inputwaiting..."
												: Objects.requireNonNull(GLFW
                                                        .glfwGetKeyName(
                                                                setting.getKeyCode(), 1))
												.toUpperCase()),
										(int) (x + 4), (int) (currentY + 4),
										settingTextColor);
					}else {
						Fonts.font.drawString(stack,
								setting.name + ": "
										+ (clicked ? "inputwaiting..." : "NONE"),
								(int) (x + 4), (int) (currentY + 4),
								settingTextColor);
					}
				}
				currentY += 18;
			}
		}
	}
	
	public void mouseClicked(double mouseX, double mouseY, int button)
	{
		if(ClickUtil.isHovered(x, y, 140, 18, mouseX, mouseY))
		{
			// default width 140 height 18
			if(button == 0)
			{
				lastX = (float)(x - mouseX);
				lastY = (float)(y - mouseY);
				dragging = true;
			}else
			{
				expand = !expand;
			}
			return;
		}
		
		if(!expand)
		{
			return;
		}
		
		double currentY = y + 18;
		for(int i = 0; i < modules.size(); i++)
		{
			Module m = modules.get(i);
			if(ClickUtil.isHovered2(x - 2, currentY, x + 122, currentY + 20,
				mouseX, mouseY))
			{
				if(button == 0)
				{
					m.toggle();
				}else
				{
					mExpand[i] = !mExpand[i];
				}
				return;
			}
			currentY += 18;
			
			if(!mExpand[i])
			{
				continue;
			}
			
			for(int j = 0; j < m.settings.size(); j++)
			{
				final Setting<?> s = m.settings.get(j);
				if(s instanceof NumberSetting)
				{
					if(ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18,
						mouseX, mouseY))
					{
						doubleSetting = (NumberSetting)s;
						return;
					}
				}else if(s instanceof ModeSetting ms)
				{
                    if(ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18,
						mouseX, mouseY))
					{
						if(button == 0)
						{
							ms.cycle();
						}else
						{
							ms.expand = !ms.expand;
						}
						return;
					}
					if(ms.expand)
					{
						for(String o : ms.modes)
						{
							currentY += 18;
							if(ClickUtil.isHovered2(x - 2, currentY, x + 122,
								currentY + 20, mouseX, mouseY))
							{
								ms.setModes(o);
								return;
							}
						}
					}
				}else if(s instanceof BooleanSetting bs)
				{
                    if(ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18,
						mouseX, mouseY))
					{
						bs.toggle();
						return;
					}
				}else if(s instanceof KeyBindSetting ks)
				{
                    if(ClickUtil.isHovered2(x, currentY, x + 120, currentY + 18,
						mouseX, mouseY))
					{
						if(button == 0)
						{
							keyBindSetting = ks;
							clicked = true;
						}else
						{
							ks.setKeyCode(0);
							keyCode = 0;
							clicked = false;
						}
						
						return;
					}
				}
				currentY += 18;
			}
		}
	}
	
	public void keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(clicked && keyBindSetting != null)
		{
			keyBindSetting.setKeyCode(keyCode);
			clicked = false;
		}
		
	}
	
	public void mouseReleased(double mouseX, double mouseY, int button)
	{
		dragging = false;
		doubleSetting = null;
	}
	
	public void mouseScrolled(double mouseX, double mouseY, double amount,
		double verticalAmount)
	{
		
	}
	
	public void onClose()
	{
		clicked = false;
	}
}
