package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;

public class NoFov extends Module
{
    public static NumberSetting fov;
    public NoFov()
    {
        super("NoFov", 0, Category.RENDER);
    }


    public void init()
    {
        super.init();
        fov = new NumberSetting("Fov", 110, 0, 170, 1);

        addSetting(fov);
    }


    @Override
    public void onEnable()
    {
     mc.options.getFovEffectScale().setValue(0D);
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

}
