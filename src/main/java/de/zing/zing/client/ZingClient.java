package de.zing.zing.client;

import de.zing.zing.client.event.EventManager;
import de.zing.zing.client.event.events.ServerJoinEvent;
import de.zing.zing.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZingClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("zing");
    public static final String MOD_NAME = "Zing";

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        EventManager.register(this);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> new ServerJoinEvent(handler).call());
    }
}
