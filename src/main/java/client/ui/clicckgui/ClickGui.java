package client.ui.clicckgui;

import client.features.modules.Module;
import client.utils.RenderingUtils;
import client.utils.animation.AnimationUtil;
import client.utils.animation.BackAnimation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ClickGui extends Screen
{
	
	private final List<ClickGuiWindow> windows = new ArrayList<>();
	private  final MinecraftClient mc  = MinecraftClient.getInstance();
	private int mouseX, mouseY;
	private float partialTicks;
	private final AnimationUtil animationUtil = new BackAnimation();
	public ClickGui()
	{
		super(Text.literal(""));
		double currentX = 50;
		for(Module.Category c : Module.Category.values())
		{
			windows.add(new ClickGuiWindow((float)currentX, 3, c));
			currentX += 150;
		}
	}


	@Override
	protected void init()
	{
		windows.forEach(ClickGuiWindow::init);
		windows.forEach(m -> m.setSize(width, height));
		animationUtil.setTick(0.25);
		super.init();
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		MatrixStack stack = context.getMatrices();
		Window window = mc.getWindow();
		partialTicks = delta;
		double per = animationUtil.uodate(0.05).calcPercent();
		setAlphaLimit(stack,(float) per);
		stack.push();

		stack.translate((float) window.getScaledWidth() / 4, (float) window.getScaledHeight() / 4, 0);
		//stack.scale(size,size, 0);
		stack.translate((float) -window.getScaledWidth() / 4, (float) -window.getScaledHeight() / 4, 0);
		windows.forEach(m -> m.render(stack, mouseX, mouseY, delta));
		stack.pop();
	}
	public static void setAlphaLimit(MatrixStack matrixStack, float alpha) {
		alpha = MathHelper.clamp(alpha * 0.01f, 0.0f, 1.0f);
		System.out.println("Alpha value: " + alpha); // Debugging
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		matrixStack.push();
		// Apply transformations (debug to check)
		matrixStack.scale(1.0f, 1.0f, 1.0f);
		matrixStack.pop();
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		windows.forEach(m -> m.mouseClicked(mouseX, mouseY, button));
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		windows.forEach(m -> m.mouseReleased(mouseX, mouseY, button));
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY,
		double horizontalAmount, double verticalAmount)
	{
		windows.forEach(m -> m.mouseScrolled(mouseX, mouseY, horizontalAmount,
			verticalAmount));
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount,
			verticalAmount);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		windows.forEach(m -> m.keyPressed(keyCode, scanCode, modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	@Override
	public void close(){
		animationUtil.setTick(0);
		super.close();
	}
}
