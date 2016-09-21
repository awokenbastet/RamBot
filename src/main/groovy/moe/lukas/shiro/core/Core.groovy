package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import moe.lukas.shiro.util.Database
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions

import java.security.MessageDigest

/**
 * Shiro's core
 *
 * Contains some commonly used helpers
 */
@CompileStatic
class Core {
    /**
     * The current discord client
     */
    private static IDiscordClient client

    /**
     * Connect and register listeners
     *
     * @param token
     */
    static void boot(String token, boolean login = true) {
        Logger.info("Connecting to Discord...")

        ClientBuilder clientBuilder = new ClientBuilder()
        clientBuilder.withToken(token)

        if (login) {
            client = clientBuilder.login()
        } else {
            client = clientBuilder.build()
        }

        EventDispatcher eventDispatcher = client.getDispatcher()
        eventDispatcher.registerListener(new EventProxy())
    }

    /**
     * Get prefix configured for $server
     * Returns scrambled UTF garbage to ensure no matches
     *
     * @param e
     * @param callback
     * @return
     */
    static String getPrefixForServer(MessageReceivedEvent e) {
        String id = e.message.channel.private ? "PRIVATE." + e.message.author.ID : e.message.guild.ID
        String prefix = Database.instance.get("prefixes", id)
        return prefix == null ? "ää\u200Böö\u180Eüü\u180E\u180Eää\u200Böö" : prefix
    }

    /**
     * Set prefix for server
     *
     * @param e
     * @param prefix
     */
    static void setPrefixForServer(MessageReceivedEvent e, String prefix) {
        String id = e.message.channel.private ? "PRIVATE." + e.message.author.ID : e.message.guild.ID
        Database.instance.set("prefixes", id, prefix.trim().replaceAll(/\s+/, ""))
    }

    /**
     * Enable typing in channel c
     *
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
     *
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
     *
     * @param c
     * @param closure
     */
    static void whileTyping(IChannel c, Closure closure) {
        enableTyping(c)
        closure()
        disableTyping(c)
    }

    /**
     * Only call c if the author of e is a guild owner
     *
     * @param e
     * @param c
     */
    static void ownerAction(MessageReceivedEvent e, Closure c) {
        if (e.message?.guild?.ownerID == e.message.author.ID || e.message.author.ID == Database.instance.get("core", "owner")) {
            c()
        } else {
            e.message.channel.sendMessage(":no_entry: Only owners are allowed to do that!")
        }
    }

    /**
     * Only call c if the author has a role that contains the ADMINISTRATOR permission
     *
     * @param e
     * @param c
     */
    static void adminAction(MessageReceivedEvent e, Closure c) {
        boolean isAdmin = e.message.author.getRolesForGuild(e.message.guild).any {
            return it.permissions.contains(Permissions.ADMINISTRATOR)
        }

        if (isAdmin) {
            c()
        } else {
            e.message.channel.sendMessage(":no_entry: Only users with ADMINISTRATOR permission are allowed to do that!")
        }
    }

    /**
     * CCTV > all
     * @param c IDiscordClient
     * @param m IMessage
     */
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

    /**
     * Create a MD5 hash of $s
     *
     * @param s
     * @return
     */
    static String hash(String s) {
        return MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
    }

    /**
     * Log out from discord
     */
    static void logout() {
        client.logout()
    }
}
