package client.event.listeners;

import client.event.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event<EventAttack> {
    Entity target;
    public EventAttack(Entity target)
    {
     this.target = target;
    }
    public Entity getTarget(){
        return target;
    }
}
