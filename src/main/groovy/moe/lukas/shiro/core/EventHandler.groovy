package moe.lukas.shiro.core

import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger
import sx.blah.discord.handle.obj.IChannel
import moe.lukas.shiro.annotations.ShiroCommand
import com.google.code.chatterbotapi.ChatterBot
import sx.blah.discord.api.events.EventSubscriber
import com.google.code.chatterbotapi.ChatterBotType
import sx.blah.discord.handle.impl.events.ReadyEvent
import com.google.code.chatterbotapi.ChatterBotFactory
import com.google.code.chatterbotapi.ChatterBotSession
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent

class EventHandler {
    private ChatterBot cleverbot = new ChatterBotFactory().create(ChatterBotType.CLEVERBOT)
    private LinkedHashMap<String, ChatterBotSession> cleverbotSessions = []

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
        if (!e.getMessage().getAuthor().isBot() && !e.getMessage().mentionsEveryone()) {

            /**
             * Check if the Owner requests a CMD change
             */
            if (e.getMessage().getContent().matches(/^SET PREFIX (.){0,5}$/)) {
                if (e.getMessage()?.getGuild()?.getOwnerID() == e.getMessage().getAuthor().getID()) {
                    Core.setPrefixForServer(e, e.getMessage().getContent().replace("SET PREFIX ", ""))
                    e.getMessage().getChannel().sendMessage("Saved :smiley:")
                } else {
                    e.getMessage().getChannel().sendMessage("Only the owner of this Guild is allowed to do this :wink:")
                }
            }
            /**
             * Show help on [prefix]h / [prefix]help
             */
            else if (
            e.getMessage().getContent().matches(/^${Core.getPrefixForServer(e)}(help|h)/) ||
                e.getMessage().getContent().matches(/^<@${e.getClient().getOurUser().getID()}>\s(help|h)$/)
            ) {
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
                                message += " | Usage: `${Core.getPrefixForServer(e)}${it.command()} ${it.usage()}`"
                            }

                            message += "\n"
                        }

                        message += "\n"
                    }
                }

                e.getMessage().getChannel().sendMessage(message)
            }
            /**
             * Catch all other commands
             */
            else {

                boolean answered = false

                ModuleLoader.modules.each { LinkedHashMap module ->
                    if (module.properties.enabled == true) {
                        // Check for matches
                        module.properties.commands.any { ShiroCommand it ->
                            if (e.getMessage().getContent().matches(
                                /^${Core.getPrefixForServer(e)}${it.command()}.*/
                            )) {
                                GroovyObject object = module["class"].newInstance()
                                object.invokeMethod("action", e)
                                System.gc()

                                answered = true

                                // break loop
                                return true
                            }
                        }
                    }
                }

                /**
                 * Forward anything else to cleverbot
                 */
                if (!answered) {
                    if (e.getMessage().getMentions().size() > 0) {
                        e.getMessage().getMentions().any {
                            if (it.getID() == e.getClient().getOurUser().getID()) {

                                IChannel channel = e.getMessage().getChannel()

                                Core.enableTyping(channel)

                                cleverbotSessions[channel.getID()] = cleverbot.createSession(Locale.ENGLISH)
                                String response = cleverbotSessions[channel.getID()].think(e.getMessage().getContent())

                                Core.disableTyping(channel)

                                channel.sendMessage(response)
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
