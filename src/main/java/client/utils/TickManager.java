package client.utils;

import client.event.listeners.EventRender2D;
import client.utils.MCUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TickManager implements MCUtil {

    static final List<Consumer<EventRender2D>> nextTickRunners = new ArrayList<>();

    public static void runOnNextRender(Consumer<EventRender2D> r) {
        nextTickRunners.add(r);
    }

    public static void render(EventRender2D event) {
        if (mc.player == null) nextTickRunners.clear();
        for (Consumer<EventRender2D> nextTickRunner : nextTickRunners) {
            nextTickRunner.accept(event);
        }
        nextTickRunners.clear();
    }
}
