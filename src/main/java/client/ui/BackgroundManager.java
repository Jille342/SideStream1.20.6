package client.ui;

import client.Client;
import client.utils.MCUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import java.io.IOException;
import java.util.Objects;

public class BackgroundManager implements MCUtil
{
	public static Identifier loadedBackgroundImage;
	
	public static void drawBackGround(DrawContext context, int width,
		int height)
	{
		
		if(loadedBackgroundImage != null)
		{
			context.drawTexture(loadedBackgroundImage, 0, 0, 0f, 0f, width,
				height, width, height);
		}
	}
	
	public static void loadBackgroundImage()
	{
		NativeImageBackedTexture image =
			new NativeImageBackedTexture(readBackgroundImage());
		loadedBackgroundImage =
			mc.getTextureManager().registerDynamicTexture("bg112", image);
		
	}
	
	public static NativeImage readBackgroundImage()
	{
		try
		{
			return NativeImage
				.read(Objects.requireNonNull(BackgroundManager.class
					.getClassLoader().getResourceAsStream(Client.bgLocation)));
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		
	}
}
