package moe.lukas.shiro.modules

import static java.util.concurrent.TimeUnit.SECONDS

import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IAdvancedModule
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.Status

@ShiroMeta(enabled = true, author = "sn0w")
class RandomGame implements IAdvancedModule {
    List<String> games = [
        // phrases
        "async is the future!",
        "spoopy stuff",
        "with senpai",
        "ʕ•ᴥ•ʔ",
        "༼ つ ◕_◕ ༽つ",
        "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧",
        "✧ﾟ･: *ヽ(◕ヮ◕ヽ)",

        // actual games
        "HuniePop",
        "Candy Crush",
        "Hyperdimension Neptunia",
        "Yu-Gi-Oh!",
        "Pokémon",
        "Final Fantasy",

        // names
        "with Shinobu-Chan",
        "with Ako-Chan",
        "with Nadeko",
        "with Miku",
        "with @Serraniel",
        "with you O_o",
        "with cats",
        "with JOHN CENA"
    ]

    void init(IDiscordClient client) {
        new Thread({
            while (true) {
                client.changeStatus(Status.game(games[new Random().nextInt(games.size())]))
                System.gc()
                SECONDS.sleep(10)
            }
        }).start()
    }

    void action(MessageReceivedEvent e) {}
}
