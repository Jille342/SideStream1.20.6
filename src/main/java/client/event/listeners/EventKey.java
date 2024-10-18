package client.event.listeners;

import client.event.Event;

public class EventKey extends Event<EventKey> {

	public int key;
	public int action;


	public EventKey(int key, String scancode, int i, int j) {
		this.key = key;
		this.action = i;
	}

	public int getCode() {
		return key;
	}

	public void setCode(int key) {
		this.key = key;
	}
}
