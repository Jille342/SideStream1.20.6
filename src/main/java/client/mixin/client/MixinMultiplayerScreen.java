package client.mixin.client;

import client.ui.gui.altmanager.screens.AltManagerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen extends Screen
{
	@Unique
	private ButtonWidget altsButton;
	
	protected MixinMultiplayerScreen(Text title)
	{
		super(title);
	}
	
	@Inject(at = @At("RETURN"), method = "init")
	private void onInitWidgetsNormal(CallbackInfo ci)
	{
		
		// add AltManager button
		addDrawableChild(altsButton = ButtonWidget
			.builder(Text.literal("Alt Manager"),
				b -> Objects.requireNonNull(client)
					.setScreen(new AltManagerScreen(this)))
			.dimensions(3, 4, 98, 20).build());
	}
	
	@Unique
	public ButtonWidget getAltsButton()
	{
		return altsButton;
	}
	
	@Unique
	public void setAltsButton(ButtonWidget altsButton)
	{
		this.altsButton = altsButton;
	}
}
