package client.event.listeners;

import client.setting.Setting;
import client.event.Event;

public class EventSettingClicked extends Event<EventSettingClicked> {

	Setting setting;

    public EventSettingClicked(Setting setting) {
        this.setting = setting;
    }

    public Setting getMessage() {
        return setting;
    }

    public void setMessage(Setting setting) {
        this.setting = setting;
    }
}
