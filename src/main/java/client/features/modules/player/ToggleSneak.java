
package client.features.modules.player;

import client.event.listeners.EventUpdate;
import client.features.modules.Module;

public class ToggleSneak extends Module
{

    public ToggleSneak()
    {
        super("ToggleSneak", 0, Category.PLAYER);

    }
    public void onUpdate(EventUpdate event){
        mc.options.sneakKey.setPressed(true);
    }
    public void onDisabled(){
        mc.options.sneakKey.setPressed(false);
    }

}
