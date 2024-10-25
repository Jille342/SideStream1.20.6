
package client.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils
{
	public static ThreadLocalRandom random = ThreadLocalRandom.current();
	
	public static boolean percent(int percent)
	{
		int random = nextInt(0, 100);
		return(percent >= random);
	}
	public static float nextFloat(float range) {
		return random.nextFloat() * range;
	}
	
	public static int nextInt(int min, int max)
	{
		int range = max - min;
		int result = min + random.nextInt(range + 1);
		return result;
	}
	public static int nextInt(int range) {
		return random.nextInt(range);
	}


	public static double nextDouble(double startInclusive, double endInclusive)
	{
		if(startInclusive == endInclusive
			|| endInclusive - startInclusive <= 0.0D)
			return startInclusive;
		return startInclusive + (endInclusive - startInclusive) * Math.random();
	}
	
	public static float nextFloat(float startInclusive, float endInclusive)
	{
		if(startInclusive == endInclusive
			|| endInclusive - startInclusive <= 0.0F)
			return startInclusive;
		return (float)(startInclusive
			+ (endInclusive - startInclusive) * Math.random());
	}
}
