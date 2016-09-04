package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.Status

@ShiroMeta(enabled = true)
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
        "Overwatch",
        "HuniePop",
        "Candy Crush",
        "Hyperdimension Neptunia",
        "Final Fantasy MCMX",

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
        Timer.setInterval(20 * 1000, {
            client.changeStatus(Status.game(games[new Random(System.nanoTime()).nextInt(games.size())]))
            System.gc()
        })
    }

    void action(MessageReceivedEvent e) {}
}
