package moe.lukas.shiro.core

import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.annotations.ShiroCommand
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent

class EventHandler {
    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onReadyEvent(ReadyEvent e) {
        Logger.info("Discord connection established!")
        ModuleLoader.load(e.client)
        Logger.info("To add me to your server visit https://discordapp.com/oauth2/authorize?client_id=${e.client.getApplicationClientID()}&scope=bot&permissions=")
    }

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onMessageReceived(MessageReceivedEvent e) {
        /**
         * Ignore other bots.
         * Shiro no likey :c
         */
        if (!e.getMessage().getAuthor().isBot()) {
            /**
             * Show help on [prefix]h / [prefix]help
             */
            if (e.getMessage().getContent().matches(/^${Core.getPrefixForServer(e)}(help|h)/)) {
                String message = ""

                message += "Hi :3 \n"
                message += "These Plugins are currently loaded:\n\n"

                ModuleLoader.modules.each { LinkedHashMap module ->
                    LinkedHashMap properties = module.properties

                    if (properties.enabled && properties.commands.size() > 0) {
                        message += "**${module.name}** by ${properties.author} "

                        if (properties.description == "") {
                            message += "[no description]\n"
                        } else {
                            message += "[${properties.description}]\n"
                        }

                        properties.commands.each { ShiroCommand it ->
                            message += "\t **${Core.getPrefixForServer(e)}${it.command()}**"

                            if (it.usage() != "") {
                                message += " - ${it.usage()}"
                            }

                            message += "\n"
                        }

                        message += "\n"
                    }
                }

                e.getMessage().getChannel().sendMessage(message)
            } else {
                /**
                 * Catch all other commands
                 */
                ModuleLoader.modules.each { LinkedHashMap module ->
                    if (module.properties.enabled == true) {
                        module.properties.commands.any { ShiroCommand it ->
                            if (e.getMessage().getContent().matches(
                                /^${Core.getPrefixForServer(e)}${it.command()}.*/
                            )) {
                                GroovyObject object = module["class"].newInstance()
                                object.invokeMethod("action", e)

                                // break loop
                                return true
                            }
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
