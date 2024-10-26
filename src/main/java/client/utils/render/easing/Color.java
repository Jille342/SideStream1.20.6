package client.utils.render.easing;

import client.utils.render.AnimationUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class Color extends EaseValue
{
	
	public float red;
	public float green;
	public float blue;
	public float alpha;
	
	public float lastRed;
	public float lastGreen;
	public float lastBlue;
	public float lastAlpha;
	
	public float easeToRed;
	public float easeToGreen;
	public float easeToBlue;
	public float easeToAlpha;
	
	public Color(float red, float green, float blue, float alpha,
		@Nullable AnimationUtil.Mode easeMode)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		
		this.lastRed = red;
		this.lastGreen = green;
		this.lastBlue = blue;
		this.lastAlpha = alpha;
		
		this.easeToRed = red;
		this.easeToGreen = green;
		this.easeToBlue = blue;
		this.easeToAlpha = alpha;
		
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null)
		{
			this.easeMode = AnimationUtil.Mode.NONE;
		}
	}
	
	public Color(java.awt.Color color, @Nullable AnimationUtil.Mode easeMode)
	{
		this(color.getRed(), color.getGreen(), color.getBlue(),
			color.getAlpha(), easeMode);
	}
	
	@Override
	public void updateEase()
	{
		float time = this.time.getCurrentMS() - this.time.getLastMS();
		this.red = lastRed + AnimationUtil.easing(easeMode, time / duration,
			easeToRed - lastRed);
		this.green = lastGreen + AnimationUtil.easing(easeMode, time / duration,
			easeToGreen - lastGreen);
		this.blue = lastBlue + AnimationUtil.easing(easeMode, time / duration,
			easeToBlue - lastBlue);
		this.alpha = lastAlpha + AnimationUtil.easing(easeMode, time / duration,
			easeToAlpha - lastAlpha);
	}
	
	public void easeTo(float red, float green, float blue, float alpha,
		float duration, boolean reset)
	{
		if(!(this.easeToRed == red && this.easeToGreen == green
			&& this.easeToBlue == blue && this.easeToAlpha == alpha))
		{
			time.reset();
			this.lastRed = this.red;
			this.lastGreen = this.green;
			this.lastBlue = this.blue;
			this.lastAlpha = this.alpha;
		}
		this.easeToRed = red;
		this.easeToGreen = green;
		this.easeToBlue = blue;
		this.easeToAlpha = alpha;
		this.duration = duration;
	}
	
	public java.awt.Color getColor()
	{
		return new java.awt.Color((int)red, (int)green, (int)blue, (int)alpha);
	}
}
