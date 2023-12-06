package de.zing.zing.client.module.modules.JoinMessage;

import net.minecraft.client.network.ClientPlayNetworkHandler;

public class SendMessageThread extends Thread {
    private final String[] messages;
    private final int sleep;
    private final ClientPlayNetworkHandler networkHandler;

    public SendMessageThread(ClientPlayNetworkHandler networkHandler, String messages, int sleep) {
        this.messages = messages.split("\n");
        this.sleep = sleep;
        this.networkHandler = networkHandler;
    }

    public void run() {
        try {
            Thread.sleep(this.sleep);
            assert networkHandler != null;
            for (String message : this.messages) {
                if (message.startsWith("/")) {
                    networkHandler.sendChatCommand(message.substring(1));
                } else {
                    networkHandler.sendChatMessage(message);
                }
                Thread.sleep(this.sleep);
            }

        } catch (InterruptedException ignored) {
        }

    }
}
