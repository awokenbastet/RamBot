package moe.lukas.shiro.core

import com.google.code.chatterbotapi.ChatterBot
import com.google.code.chatterbotapi.ChatterBotFactory
import com.google.code.chatterbotapi.ChatterBotSession
import com.google.code.chatterbotapi.ChatterBotType
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.obj.IChannel

class EventHandler {
    private ChatterBot cleverbot = new ChatterBotFactory().create(ChatterBotType.CLEVERBOT)
    private LinkedHashMap<String, ChatterBotSession> cleverbotSessions = []
    private List<IChannel> ignoredSources = []

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
        if (ignoredSources.any { return it.getID() == e.message.channel.getID() }) {
            if (e.message.content == "UNIGNORE THIS CHANNEL") {
                Core.ownerAction(e, {
                    ignoredSources.remove(e.message.channel)
                    e.message.channel.sendMessage(":speaker: Ok I will watch this channel again!")
                })
            }
        } else {
            /**
             * Ignore other bots.
             * Shiro no likey :c
             */
            if (!e.message.author.bot && !e.message.mentionsEveryone()) {
                /**
                 * Check if the Owner requests a CMD change
                 */
                if (e.message.content.matches(/^SET PREFIX (.){0,5}$/)) {
                    if (e.message?.guild?.ownerID == e.message.author.getID()) {
                        Core.setPrefixForServer(e, e.message.content.replace("SET PREFIX ", ""))
                        e.message.channel.sendMessage("Saved :smiley:")
                    } else {
                        e.message.channel.sendMessage("Only the owner of this Guild is allowed to do this :wink:")
                    }
                }
                /**
                 * Show help on [prefix]h / [prefix]help
                 */
                else if (
                e.message.content.matches(/^${Core.getPrefixForServer(e)}(help|h)/) ||
                    e.message.content.matches(/^<@${e.getClient().getOurUser().getID()}>\s(help|h)$/)
                ) {
                    String message = ""

                    message += "Hi :3 \n"
                    message += "These Plugins are currently loaded:\n\n"

                    ModuleLoader.modules.each { LinkedHashMap module ->
                        LinkedHashMap properties = module.properties

                        if (properties.enabled && properties.commands.size() > 0) {
                            message += "**${module.name}** "

                            if (properties.description == "") {
                                message += "[no description]\n"
                            } else {
                                message += "[${properties.description}]\n"
                            }

                            properties.commands.each { ShiroCommand it ->
                                message += "\t **${Core.getPrefixForServer(e)}${it.command()}**"

                                if (it.usage() != "") {
                                    message += " `${it.usage()}`"
                                }

                                message += "\n"
                            }

                            message += "\n"
                        }
                    }

                    e.message.channel.sendMessage(message)
                }
                /**
                 * Catch all other commands
                 */
                else {

                    boolean answered = false

                    // Check for command matches
                    ModuleLoader.modules.each { LinkedHashMap module ->
                        if (module.properties.enabled == true) {
                            module.properties.commands.any { ShiroCommand it ->
                                if (e.message.content.matches(
                                    /^${Core.getPrefixForServer(e)}${it.command()}\s.*/
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
                        switch (e.message.content) {
                            case "IGNORE THIS CHANNEL":
                                Core.ownerAction(e, {
                                    e.message.channel.sendMessage(":mute: Ok I will ignore this channel now!")
                                    ignoredSources.push(e.message.channel)
                                })
                                break

                            case "REFRESH CHAT SESSION":
                                Core.ownerAction(e, {
                                    cleverbotSessions[e.message.channel.getID()] = cleverbot.createSession(Locale.ENGLISH)
                                    e.message.channel.sendMessage("Done :smiley:")
                                })
                                break
                        }
                    } else if (e.message.mentions.size() > 0) {
                        e.message.mentions.any {
                            if (it.getID() == e.client.ourUser.getID()) {
                                String response = null
                                IChannel channel = e.message.channel

                                Core.whileTyping(channel, {
                                    cleverbotSessions[channel.getID()] = cleverbot.createSession(Locale.ENGLISH)
                                    response = cleverbotSessions[channel.getID()].think(e.message.getContent())
                                })

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
