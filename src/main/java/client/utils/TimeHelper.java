package client.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TimeHelper
{
	
	private long lastMS;
	
	public long getCurrentMS()
	{
		return System.currentTimeMillis();
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
		this.lastMS = this.getCurrentMS();
	}

}
