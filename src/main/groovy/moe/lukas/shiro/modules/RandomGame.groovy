package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.Status

@ShiroMeta(enabled = true)
@CompileStatic
class RandomGame implements IAdvancedModule {
    List<String> games = [
        // phrases
        "async is the future!",
        "down with OOP!",
        "spoopy stuff",

        // Kaomoji
        "ʕ•ᴥ•ʔ",
        "༼ つ ◕_◕ ༽つ",
        "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧",
        "( ͡° ͜ʖ ͡°)",
        "¯\\_(ツ)_/¯",
        "(ง ͠° ͟ل͜ ͡°)ง",
        "ಠ_ಠ",
        "(╯°□°)╯︵ ʞooqǝɔɐɟ",
        "♪~ ᕕ(ᐛ)ᕗ",
        "\\ (•◡•) /",
        "｡◕‿◕｡",

        // actual games
        "Hearthstone",
        "Overwatch",
        "HuniePop",
        "Candy Crush",
        "Hyperdimension Neptunia",
        "Final Fantasy MCMX",

        // software
        "with FFMPEG",
        "with libav",
        "with groovy",
        "with apache",
        "with python",
        "with Reflections",

        // names
        "with Shinobu-Chan",
        "with Ako-Chan",
        "with Nadeko",
        "with Miku",
        "with you O_o",
        "with cats",
        "with JOHN CENA",
        "with senpai",
        "with Serraniel#8978",
        "with FADED#3237"
    ]

    void init(IDiscordClient client) {
        Timer.setInterval(20 * 1000, {
            client.changeStatus(Status.game(games[new Random(System.nanoTime()).nextInt(games.size())]))
            System.gc()
        })
    }

    void action(MessageReceivedEvent e) {}
}
