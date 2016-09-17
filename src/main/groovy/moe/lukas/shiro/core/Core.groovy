package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import moe.lukas.shiro.util.Database

import java.security.MessageDigest

import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage

/**
 * Shiro's core
 */
@CompileStatic
class Core {
    /**
     * The current discord client
     */
    private static IDiscordClient client

    /**
     * Connect to the discord api
     * @param token
     * @param login
     */
    private static void connect(String token, boolean login = true) {
        Logger.info("Connecting to Discord...")

        ClientBuilder clientBuilder = new ClientBuilder()
        clientBuilder.withToken(token)

        if (login) {
            client = clientBuilder.login()
        } else {
            client = clientBuilder.build()
        }
    }

    /**
     * Register all listener classes
     */
    private static void registerListeners() {
        EventDispatcher eventDispatcher = client.getDispatcher()
        eventDispatcher.registerListener(new EventHandler())
    }

    /**
     * Connect and register shorthand
     * @param token
     */
    static void boot(String token) {
        connect(token)
        registerListeners()
    }

    /**
     * Get prefix configured for $server
     * @param e
     * @param callback
     * @return
     */
    static String getPrefixForServer(MessageReceivedEvent e, fallback = true) {
        def id = e.message.channel.private ? "PRIVATE." + e.message.author.ID : e.message.guild.ID

        String prefix = Database.instance.get("prefixes", id)

        if (prefix == null && fallback) {
            e.getMessage().getChannel().sendMessage('''
Warning :warning:\n
There is no configured prefix for your guild!\n
I will fallback to `%`
Please tell your server owner to set a new command prefix using `SET PREFIX <your prefix>`
''')
            Database.instance.set("prefixes", id, "%")
            return "%"
        } else {
            return prefix
        }
    }

    /**
     * Set prefix for server
     * @param e
     * @param prefix
     */
    static void setPrefixForServer(MessageReceivedEvent e, String prefix) {
        def id = e.message.channel.private ? "PRIVATE." + e.message.author.ID : e.message.guild.ID
        Database.instance.set("prefixes", id, prefix)
    }

    /**
     * Enable typing in channel c
     * @param c
     */
    static void enableTyping(IChannel c) {
        try {
            c.toggleTypingStatus()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * Disable typing in channel c
     * @param c
     */
    static void disableTyping(IChannel c) {
        try {
            !c.getTypingStatus() ?: c.toggleTypingStatus()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * Type while the closure runs
     * @param c
     * @param closure
     */
    static void whileTyping(IChannel c, Closure closure) {
        enableTyping(c)
        closure.call()
        disableTyping(c)
    }

    /**
     * Only call c if the author of e is a guild owner
     * @param e
     * @param c
     */
    static void ownerAction(MessageReceivedEvent e, Closure c) {
        if (e.message?.guild?.ownerID == e.message.author.ID || e.message.author.ID == Database.instance.get("core", "owner")) {
            c.call()
        } else {
            e.message.channel.sendMessage(":no_entry: Only owners are allowed to do that!")
        }
    }

    /**
     * CCTV > all
     * @param c IDiscordClient
     * @param m IMessage
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    static void cctv(MessageReceivedEvent e) {
        IDiscordClient c = e.client
        IMessage m = e.message

        if (Database.instance.get("core", "cctv.enabled", true) as boolean) {
            IChannel channel = c?.
                    getGuildByID(Database.instance.get("core", "cctv.server", "180818466847064065") as String)?.
                    getChannelByID(Database.instance.get("core", "cctv.channel", "221215096842485760") as String)

            if (channel != null) {
                channel.sendMessage(
                        ":cool: A new message! \n" +
                                "```\n" +
                                "At: ${m.timestamp}\n" +
                                "Origin: #${m.channel.name} in ${m.channel?.guild?.name} " +
                                "(${m.channel?.guild?.ID}:${m.channel.ID}) \n" +
                                "Author: ${m.author.name}#${m.author.discriminator} (Nick: ${m.author.getNicknameForGuild(m.channel.guild)})\n" +
                                "Roles: ${m.author.getRolesForGuild(m.channel?.guild).join(",")} \n" +
                                "Message:\n ${m.content}\n" +
                                "```"
                )
            }
        }
    }

    static String hash(String s) {
        return MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
    }

    static void logout() {
        client.logout()
    }
}
