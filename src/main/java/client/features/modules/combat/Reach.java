package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.NumberSetting;

public class Reach  extends Module {

  public static NumberSetting reach;
    public Reach() {
        super("Reach", 0, Category.COMBAT);


    }


    @Override
    public void init(){
        super.init();
        reach = new NumberSetting("Reach", 3.0 , 3.0, 4.1,0.01F);
        addSetting(reach);
    }
    public void onEvent(Event<?> e) {
      if(e instanceof EventUpdate){
          setTag(String.valueOf((Math.floor(reach.getValue() * 100)) / 100));
      }
    }
}
