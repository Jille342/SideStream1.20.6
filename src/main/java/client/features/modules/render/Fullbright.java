package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.mixin.mixininterface.ISimpleOption;
import net.minecraft.client.option.SimpleOption;

public class Fullbright extends Module
{
	public Fullbright()
	{
		super("Fullbright", 0, Category.RENDER);
	}
	
	double lastGamma;
	
	@Override
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate)
		{
			setGamma(1000D);
		}
		super.onEvent(e);
	}
	
	@Override
	public void onEnabled()
	{
		lastGamma = (Double)mc.options.getGamma().getValue();
		super.onEnabled();
	}
	
	@Override
	public void onDisabled()
	{
		mc.options.getGamma().setValue(lastGamma);
		super.onDisabled();
	}
	
	public void setGamma(double value)
	{
		SimpleOption<Double> gammaOption = mc.options.getGamma();
		ISimpleOption<Double> gammaOption2 = ISimpleOption.get(gammaOption);
		gammaOption2.forceSetValue(value);
	}
}
