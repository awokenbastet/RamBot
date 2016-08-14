package moe.lukas.shiro.core

import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent

class EventHandler {
    @EventSubscriber
    @SuppressWarnings("GrMethodMayBeStatic")
    void onReadyEvent(ReadyEvent e) {
        Logger.info("Discord connection established!")
        ModuleLoader.load()
    }

    @EventSubscriber
    @SuppressWarnings("GrMethodMayBeStatic")
    void onMessageReceived(MessageReceivedEvent e) {
        Logger.info(e.getMessage().getContent())
    }

    @EventSubscriber
    @SuppressWarnings("GrMethodMayBeStatic")
    void onOffline(DiscordDisconnectedEvent e) {
        Logger.warn("Discord gateway disconnected!")
        Logger.warn("")
    }
}
