package moe.lukas.shiro.core

import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher

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

        if(login) {
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
}
