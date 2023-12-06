package de.zing.zing.client.module;

import de.zing.zing.client.Json;
import de.zing.zing.client.event.EventManager;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public abstract class Module {
    protected final MinecraftClient mc;
    private final String name;
    private boolean enabled;
    //TODO: control with packets
    private Boolean serverControlled = null;

    public Module(String name) {
        this.name = name;
        this.mc = MinecraftClient.getInstance();
        this.loadEnabled();
    }

    public boolean isEnabled() {
        if (serverControlled != null) {
            return serverControlled;
        }
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Json.writeToFile(this.getName(), "isEnabled", enabled);
        if (enabled && serverControlled != Boolean.FALSE) {
            EventManager.register(this);
        } else if (serverControlled != Boolean.TRUE) {
            EventManager.unregister(this);
        }
    }

    public Module setEnabledDebug(boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    public String getName() {
        return name;
    }

    private void loadEnabled() {
        this.enabled = Objects.requireNonNullElse(Json.getFromFile(this.getName(), "isEnabled", Boolean.class), false);
        this.setEnabled(this.enabled);
    }
}
