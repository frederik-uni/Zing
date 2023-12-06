package de.zing.zing.client.module.modules.JoinMessage;

import de.zing.zing.client.Json;
import de.zing.zing.client.event.EventTarget;
import de.zing.zing.client.event.events.ServerJoinEvent;
import de.zing.zing.client.module.Module;
import net.minecraft.client.network.ServerInfo;

import java.util.HashMap;
import java.util.Objects;

/**
 * More in AddServerScreenMixin
 */
public class SendMessageModule extends Module {
    private final HashMap<String, String> messageMap;

    public SendMessageModule() {
        super("SendMessage");
        this.messageMap = loadMessagesFromFile();
    }

    private HashMap<String, String> loadMessagesFromFile() {
        return Json.getFromFile(this.getName(), "messageMap", HashMap.class);
    }

    public String get(ServerInfo key) {
        return messageMap.computeIfAbsent(generateIdent(key), k -> "");
    }

    public void updateIdent(ServerInfo old, String name, String address) {
        messageMap.put(generateIdent(name, address), messageMap.remove(generateIdent(old)));
        Json.writeToFile(this.getName(), "messageMap", messageMap);
    }

    public void updateValue(ServerInfo key, String value) {
        messageMap.put(generateIdent(key), value);
        Json.writeToFile(this.getName(), "messageMap", messageMap);
    }

    private String generateIdent(ServerInfo value) {
        return generateIdent(value.name, value.address);
    }

    private String generateIdent(String name, String address) {
        return String.format("%s:%s", name, address);
    }

    @EventTarget
    public void onJoin(ServerJoinEvent e) {
        String message = this.messageMap.get(generateIdent(Objects.requireNonNull(mc.getCurrentServerEntry())));
        if (message == null || message.isEmpty()) {
            return;
        }
        new SendMessageThread(e.handler, message, 100).start();

    }
}
