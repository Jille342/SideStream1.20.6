package client.event.listeners;

import client.event.Event;
import lombok.Getter;
import net.minecraft.client.input.Input;

public class EventInput extends Event<EventInput> {
    @Getter
	private final Input input;

    @Getter
	private final float slowDownFactor;
    public boolean moveFix = false;

    public EventInput(Input input, float slowDownFactor) {
        this.input = input;
        this.slowDownFactor = slowDownFactor;
    }

}
