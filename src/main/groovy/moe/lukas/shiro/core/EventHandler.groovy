package moe.lukas.shiro.core

import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent

class EventHandler {
    @SuppressWarnings("GrMethodMayBeStatic")
    @EventSubscriber
    void onReadyEvent(ReadyEvent e) {
        Logger.info("Discord connection established!")
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    @EventSubscriber
    void onMessageReceived(MessageReceivedEvent e) {
        Logger.info(e.getMessage.getContent)
    }
}
