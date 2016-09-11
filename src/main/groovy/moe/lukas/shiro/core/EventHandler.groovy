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
    private HashMap<String, ChatterBotSession> cleverbotSessions = []

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onReadyEvent(ReadyEvent e) {
        int servers = e.client.guilds.size()
        int channels = 0

        e.client.guilds.each { it.channels.each { channels++ } }

        Logger.info("Discord connection established!")
        Logger.info("I'm connected to ${servers} servers and listening on ${channels} channels :) \n")
        ModuleLoader.load(e.client)
        Logger.info("To add me to your server visit https://discordapp.com/oauth2/authorize?client_id=${e.client.getApplicationClientID()}&scope=bot&permissions=")
    }

    @EventSubscriber
    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    void onMessageReceived(MessageReceivedEvent e) {
        /**
         * Ignore other bots and @everyone/@here
         */
        if (!e.message.author.bot && !e.message.mentionsEveryone()) {

            /**
             * Check if the message contains a @mention
             */
            if (e.message.mentions.size() > 0) {
                e.message.mentions.any {
                    if (it.ID == e.client.ourUser.ID) {
                        Core.cctv(e)

                        /**
                         * Process @mention command
                         * Send the message to cleverbot if nothing matches
                         */
                        switch (e.message.content.split(" ").drop(1).join(" ")) {
                            case ~/^REFRESH CHAT SESSION$/:
                                Core.ownerAction(e, {
                                    cleverbotSessions[e.message.channel.ID] = cleverbot.createSession(Locale.ENGLISH)
                                    e.message.channel.sendMessage("Done :smiley:")
                                })
                                break

                            case ~/^SET PREFIX (.){0,5}$/:
                                Core.ownerAction(e, {
                                    Core.setPrefixForServer(
                                        e,
                                        e.message.content
                                            .replace("SET PREFIX ", "")
                                            .replace("<@${e.client.ourUser.ID}>", "")
                                    )
                                    e.message.channel.sendMessage("Saved :smiley:")
                                })
                                break

                            default:
                                sendToCleverbot(e)
                                break
                        }

                        return true
                    }
                }
            }
            /**
             * Check if the channel is private
             */
            else if (e.message.channel.private) {
                Core.cctv(e)
                sendToCleverbot(e)
            }
            /**
             * Check if a module matches
             */
            else {
                ModuleLoader.modules.each { HashMap module ->
                    if (module.properties.enabled == true) {
                        module.properties.commands.any { ShiroCommand it ->
                            switch (e.message.content) {
                                case ~/^${Core.getPrefixForServer(e)}${it.command()}\s.*/:
                                case ~/^${Core.getPrefixForServer(e)}${it.command()}$/:
                                    def action = {
                                        Core.cctv(e)
                                        module["class"].invokeMethod("action", e)
                                    }

                                    if (it.adminOnly()) {
                                        Core.ownerAction(e, action)
                                    } else {
                                        action()
                                    }

                                    // break loop and switch
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
        System.exit(2)
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
}
