package moe.lukas.shiro.core

import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher

class Core {
    private static IDiscordClient client;

    private static void connect(String token, boolean login = true) {
        ClientBuilder clientBuilder = new ClientBuilder()
        clientBuilder.withToken(token)

        if(login) {
            client = clientBuilder.login()
        } else {
            client = clientBuilder.build()
        }
    }

    private static void registerListeners() {
        EventDispatcher eventDispatcher = client.getDispatcher()
        eventDispatcher.registerListener(EventHandler)
    }

    static void boot(String token) {
        connect(token)
        registerListeners()
    }
}
