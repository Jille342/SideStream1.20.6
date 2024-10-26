package client.settings;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class NumberSetting extends Setting
{
	
	public double value, minimum, maximum, increment;
	
	public NumberSetting(String name, Supplier<Boolean> visibility,
		double value, double minimum, double maximum, double increment)
	{
		super(name, visibility, value);
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
	}
	
	public NumberSetting(String name, double value, double minimum,
		double maximum, double increment)
	{
		super(name, null, value);
		this.name = name;
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
	}
	
	public double getFlooredValue()
	{
		return(Math.floor(value * 100) / 100);
	}
	
	public void setValue(double value)
	{
		double preci = 1 / increment;
		this.value =
			Math.round(Math.max(minimum, Math.min(maximum, value)) * preci)
				/ preci;
	}
	
	public void setValue(float posX, float width, float mouseX)
	{
		this.setValue((mouseX - posX) * (this.maximum - this.minimum) / width
			+ this.minimum);
	}
	
	public double getPercentage()
	{
		return (value - minimum) / (maximum - minimum);
	}
	
	public void increment(boolean positive)
	{
		setValue(getValue() + (positive ? 1 : -1) * increment);
	}
}
