package client.event.listeners;

import client.event.Event;
import net.minecraft.client.input.Input;

public class EventInput extends Event<EventInput> {
    private final Input input;

    private final float slowDownFactor;
    public boolean moveFix = false;

    public EventInput(Input input, float slowDownFactor) {
        this.input = input;
        this.slowDownFactor = slowDownFactor;
    }

    public Input getInput(){
        return input;
    }

    public float getSlowDownFactor() {
        return slowDownFactor;
    }
}
