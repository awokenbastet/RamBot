package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent

class EventHandler {
    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onReadyEvent(ReadyEvent e) {
        Logger.info("Discord connection established!")
        ModuleLoader.load()
    }

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getMessage().getAuthor().isBot()) {
            ModuleLoader.modules.each { LinkedHashMap module ->
                if (module.properties.enabled == true) {
                    ShiroCommand[] commands = module.properties.commands
                    commands.any { ShiroCommand it ->
                        if (e.getMessage().getContent().matches(/^${Core.getPrefixForServer(e)}${it.command()}.*/)) {
                            IModule mod = module.instance
                            mod.action(e)
                            return true
                        }
                    }
                }
            }
        }
    }

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onOffline(DiscordDisconnectedEvent e) {
        Logger.warn("Discord gateway disconnected!")
        Logger.warn("")
    }
}
