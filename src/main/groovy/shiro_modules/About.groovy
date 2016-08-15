package shiro_modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IMessage

@ShiroMeta(
    enabled = true,
    description = "More information about Shiro",
    author = "sn0w",
    commands = [
        @ShiroCommand(command = "about"),
        @ShiroCommand(command = "a")
    ]
)
class About extends IModule {
    static void action(IMessage e, IDiscordClient client) {
        client.getOrCreatePMChannel(e.getAuthor()).sendMessage('''
Oh you want to know more about me? :3
Click here -> http://no-game-no-life.wikia.com/wiki/Shiro
        ''')
    }
}
