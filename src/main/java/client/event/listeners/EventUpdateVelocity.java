package client.event.listeners;


import client.event.Event;

public class EventUpdateVelocity extends Event<EventUpdateVelocity> {
    public float speed;
    public float yaw;


    public EventUpdateVelocity(float speed, float yaw) {
        this.speed = speed;
        this.yaw = yaw;
    }
}
