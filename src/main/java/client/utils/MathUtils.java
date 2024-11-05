package client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils
{
	
	public static double getDistanceSq(double[] vec)
	{
		double dist = 0;
		
		for(double d : vec)
		{
			dist += Math.pow(d, 2);
		}
		
		return Math.sqrt(dist);
	}
	
	public static double getDistanceAd(double[] vec)
	{
		double dist = 0;
		
		for(double d : vec)
		{
			dist += d;
		}
		
		return dist;
	}
	
	public static double round(final double n, final int n2)
	{
		if(n2 < 0)
		{
			throw new IllegalArgumentException();
		}
		return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP)
			.doubleValue();
	}

	public static double clamp(double value, double min, double max) { if (value < min) { return min; } else if (value > max) { return max; } else { return value; } }
}
