package client.utils.render.easing;

import client.utils.render.AnimationUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
public class Value extends EaseValue
{
	
	public float value;
	@Setter
	public float lastValue;
	@Setter
	public float easeTo;
	
	public Value(double value, @Nullable AnimationUtil.Mode easeMode)
	{
		this.value = (float)value;
		this.lastValue = (float)value;
		this.easeTo = (float)value;
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null)
		{
			this.easeMode = AnimationUtil.Mode.NONE;
		}
	}
	
	public Value(float value, @Nullable AnimationUtil.Mode easeMode)
	{
		this.value = value;
		this.lastValue = value;
		this.easeTo = value;
		this.duration = 1;
		this.easeMode = easeMode;
		if(easeMode == null)
		{
			this.easeMode = AnimationUtil.Mode.NONE;
		}
	}

	public void setValue(float value)
	{
		this.value = value;
		this.lastValue = value;
	}
	
	@Override
	public void updateEase()
	{
		long time = this.time.getCurrentMS() - this.time.getLastMS();
		this.value = lastValue + AnimationUtil.easing(easeMode, time / duration,
			easeTo - lastValue);
		if(Math.abs(value - easeTo) < 1 / duration)
		{
			this.value = easeTo;
		}
	}
	
	public void easeTo(float value, float duration, boolean reset)
	{
		if(this.easeTo != value)
		{
			time.reset();
			this.lastValue = this.value;
		}
		this.easeTo = value;
		this.duration = duration;
	}
	
	public enum num
	{
		ZERO(0),
		ONE(1),
		TEN(10);
		
		public final Value value;
		
		num(float value)
		{
			this.value = new Value(value, null);
		}
	}
}
