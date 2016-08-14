package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.util.Brain
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
        ModuleLoader.each { LinkedHashMap module ->
            String prefix = Brain.instance.get("prefixes.${e.getMessage().getGuild().getID()}")

            if (prefix == null) {
                e.getMessage().getChannel().sendMessage('''
Warning :warning:
There is no configured prefix for your guild!
I will fallback to `+#+` (very uncommon)
Please tell your server owner to set a new command prefix using `+#+PREFIX <your prefix>`
''')
                Brain.instance.set("prefixes.${e.getMessage().getGuild().getID()}", "+#+")
            } else {
                module.properties?.commands?.each { ShiroCommand c ->
                    /**
                     * @todo check if command matches and execute $action()
                     */
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
