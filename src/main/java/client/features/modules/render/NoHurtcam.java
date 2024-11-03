package client.features.modules.render;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;

public class NoHurtcam extends Module {
    double lastGamma;

    public NoHurtcam() {
        super("NoHurtcam", 0, Category.RENDER);
    }

    @Override
    public void onEnabled() {

        super.onEnabled();
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {

        }
        super.onEvent(e);
    }

}
