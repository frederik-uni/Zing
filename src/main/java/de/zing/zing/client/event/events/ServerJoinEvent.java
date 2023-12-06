package de.zing.zing.client.event.events;

import de.zing.zing.client.event.Event;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class ServerJoinEvent extends Event {
    public ClientPlayNetworkHandler handler;

    public ServerJoinEvent(ClientPlayNetworkHandler handler) {
        this.handler = handler;
    }
}
