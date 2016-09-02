package moe.lukas.shiro.core

import com.google.code.chatterbotapi.ChatterBot
import com.google.code.chatterbotapi.ChatterBotFactory
import com.google.code.chatterbotapi.ChatterBotSession
import com.google.code.chatterbotapi.ChatterBotType
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage

class EventHandler {
    private ChatterBot cleverbot = new ChatterBotFactory().create(ChatterBotType.CLEVERBOT)
    private LinkedHashMap<String, ChatterBotSession> cleverbotSessions = []

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onReadyEvent(ReadyEvent e) {
        int servers = e.client.guilds.size()
        int channels = 0

        e.client.guilds.each {
            it.channels.each {
                channels++
            }
        }

        Logger.info("Discord connection established!")
        Logger.info("I'm connected to ${servers} servers and listening on ${channels} channels :) \n")
        ModuleLoader.load(e.client)
        Logger.info("To add me to your server visit https://discordapp.com/oauth2/authorize?client_id=${e.client.getApplicationClientID()}&scope=bot&permissions=")
    }

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onMessageReceived(MessageReceivedEvent e) {
        if (e.message.channel.private) {
            Core.setPrefixForServer(e, "%")
        }

        /**
         * Ignore other bots.
         * Shiro no likey :c
         */
        if (!e.message.author.bot && !e.message.mentionsEveryone()) {
            if (Brain.instance.get("cctv.enabled", true) as boolean) {
                this.cctv(e.client, e.message)
            }

            /**
             * Check if the Owner requests a CMD change
             */
            if (e.message.content.matches(/^SET PREFIX (.){0,5}$/)) {
                if (e.message?.guild?.ownerID == e.message.author.ID) {
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
                    e.message.content.matches(/^<@${e.getClient().getOurUser().ID}>\s(help|h)$/)
            ) {
                String message = "```\n"

                ModuleLoader.modules.each { LinkedHashMap module ->
                    LinkedHashMap properties = module.properties

                    if (properties.enabled && properties.commands.size() > 0) {
                        message += "${module.name} "

                        if (properties.description == "") {
                            message += "[no description]"
                        } else {
                            message += "[${properties.description}]"
                        }

                        message += "\n"

                        properties.commands.each { ShiroCommand it ->
                            message += "\t ${Core.getPrefixForServer(e)}${it.command()} "

                            if (it.usage() != "") {
                                message += "${it.usage()}"
                            }

                            message += "\n"
                        }

                        message += "\n"
                    }
                }

                message += "```"

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
                            if (
                            e.message.content.matches(/^${Core.getPrefixForServer(e)}${it.command()}\s.*/) ||
                                    e.message.content.matches(/^${Core.getPrefixForServer(e)}${it.command()}$/)
                            ) {
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
                        case "REFRESH CHAT SESSION":
                            Core.ownerAction(e, {
                                cleverbotSessions[e.message.channel.ID] = cleverbot.createSession(Locale.ENGLISH)
                                e.message.channel.sendMessage("Done :smiley:")
                            })
                            break

                        default:
                            if (e.message.mentions.size() > 0) {
                                e.message.mentions.any {
                                    if (it.ID == e.client.ourUser.ID) {
                                        sendToCleverbot(e)
                                        return true
                                    }
                                }
                            } else if (e.message.channel.private) {
                                sendToCleverbot(e)
                            }
                            break
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

    /**
     * Helper that sends e to cleverbot
     * @param e
     */
    private void sendToCleverbot(MessageReceivedEvent e) {
        String response = null
        IChannel channel = e.message.channel
        def id = channel.private ? e.message.author.ID : e.message.channel.ID

        Core.whileTyping(channel, {
            cleverbotSessions[id] = cleverbot.createSession(Locale.ENGLISH)
            response = cleverbotSessions[id].think(e.message.getContent())
        })

        channel.sendMessage(response == "" ? ":grey_question:" : response)
    }

    /**
     * CCTV > all
     * @param c IDiscordClient
     * @param m IMessage
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    private void cctv(IDiscordClient c, IMessage m) {
        IChannel channel = c?.
                getGuildByID(Brain.instance.get("cctv.server", "180818466847064065") as String)?.
                getChannelByID(Brain.instance.get("cctv.channel", "221215096842485760") as String)

        if (channel != null) {
            channel.sendMessage(
                    ":cool: A new message! \n" +
                            "```\n" +
                            "Origin: #${m.channel.name} in ${m.channel.guild.name} " +
                            "(${m.channel.guild.ID}:${m.channel.ID}) \n" +
                            "Author: ${m.author.name}#${m.author.discriminator}\n" +
                            "Content: ${m.content}\n" +
                            "```"
            )
        }
    }
}
