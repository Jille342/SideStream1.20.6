package client.utils.render.easing;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Time
{
	
	@Setter
	private long lastMS;
	private long currentMS = 0L;
	
	public void update(long deltaTime)
	{
		currentMS += deltaTime;
	}
	
	public Time()
	{
		this.lastMS = 0L;
	}

	public boolean hasReached(float f)
	{
		return (float)(this.getCurrentMS() - this.lastMS) >= f;
	}
	
	public boolean hasReached(double f)
	{
		return (double)(this.getCurrentMS() - this.lastMS) >= f;
	}
	
	public boolean hasReached(long f)
	{
		return (float)(this.getCurrentMS() - this.lastMS) >= (float)f;
	}
	
	public void reset()
	{
		this.currentMS = 0L;
		this.lastMS = 0L;
	}

}
