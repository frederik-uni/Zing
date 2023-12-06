package de.zing.zing.client.event;

import de.zing.zing.client.ZingClient;

import java.util.ArrayList;

public class Event {

    public Event call() {
        final ArrayList<EventData> dataList = EventManager.get(this.getClass());

        if (dataList != null) {
            for (EventData data : dataList) {
                try {
                    data.target.invoke(data.source, this);
                } catch (Exception e) {
                    ZingClient.LOGGER.error(e.toString());
                }
            }
        }
        return this;
    }
}