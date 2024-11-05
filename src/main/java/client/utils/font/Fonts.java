package client.utils.font;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Fonts
{
	public static TTFFontRenderer font = TTFFontRenderer.of("ElliotSans", 10);
	public static TTFFontRenderer font2 = TTFFontRenderer.of("ElliotSans", 6);
	public static TTFFontRenderer titleFont =
		TTFFontRenderer.of("ElliotSans", 15);
	
}
