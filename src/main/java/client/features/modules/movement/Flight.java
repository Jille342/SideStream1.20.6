package client.features.modules.movement;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;


public class Flight extends Module {

ModeSetting mode;
    NumberSetting speed;
    int bypassTimer = 0;
    BooleanSetting bypassVanilla;

    public Flight() {
        super("Flight", 0, Category.MOVEMENT);
        mode = new ModeSetting( "Mode", "Vanilla", "Vanilla", "CustomVanilla");
        bypassVanilla = new BooleanSetting("Bypass Vanilla", true);
        speed = new NumberSetting("Speed", 2,0, 10,1);
    addSetting(mode, speed,bypassVanilla);
    }

  public void onEvent(Event<?> event) {
      if (event instanceof EventUpdate) {
          setTag(mode.getValue());
          if (bypassVanilla.getValue()) {
              bypassTimer++;
              if (bypassTimer > 10) {
                  bypassTimer = 0;
                  Vec3d p = mc.player.getPos();
                  mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y - 0.2, p.z, false));
                  mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y + 0.2, p.z, false));
              }
          }
          switch (mode.getValue()) {
              case "Vanilla":
                  mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
                  break;
              case "CustomVanilla":
                  mc.player.getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
                  mc.player.getAbilities().flying = true;
                  break;
          }
      }
  }

    @Override
    public void onDisabled() {
        mc.player.getAbilities().flying = false;
        super.onDisabled();
    }
}