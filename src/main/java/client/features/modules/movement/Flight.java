package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.ModeSetting;

import java.util.Arrays;

public class Flight extends Module {

ModeSetting mode;

    public Flight() {
        super("Flight", 0, Category.MOVEMENT);
        mode = new ModeSetting( "Mode", "Vanilla", "Vanilla");
    addSetting(mode);
    }

  public void onEvent(Event<?> event) {
      if (event instanceof EventUpdate) {
          switch (mode.getValue()) {
              case "Vanilla":
                  mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
                  break;
          }
      }
  }
}