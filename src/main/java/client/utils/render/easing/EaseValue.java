package client.utils.render.easing;

import client.utils.render.AnimationUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EaseValue
{
	
	public EaseValue()
	{
		this.time = new Time();
	}

	public float duration;
	public AnimationUtil.Mode easeMode;
	public Time time;
	
	public abstract void updateEase();
	
}
