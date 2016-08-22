package moe.lukas.shiro.core

import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

/**
 * Shiro's core
 */
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
    static String getPrefixForServer(MessageReceivedEvent e) {
        String prefix = Brain.instance.get("prefixes.${e.getMessage().getGuild().getID()}")

        if (prefix == null) {
            e.getMessage().getChannel().sendMessage('''
Warning :warning:\n
There is no configured prefix for your guild!\n
I will fallback to `%`
Please tell your server owner to set a new command prefix using `SET PREFIX <your prefix>`
''')
            Brain.instance.set("prefixes.${e.getMessage().getGuild().getID()}", "%")
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
        Brain.instance.set("prefixes.${e.getMessage().getGuild().getID()}", prefix)
    }

    /**
     * Enable typing in channel c
     * @param c
     */
    static void enableTyping(IChannel c) {
        try {
            c.toggleTypingStatus()
        } catch (Exception ex) {

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

        }
    }
}
