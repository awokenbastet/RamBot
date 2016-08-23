package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "More information about Shiro",
    author = "sn0w",
    commands = [
        @ShiroCommand(command = "about"),
        @ShiroCommand(command = "a")
    ]
)
class About implements IModule {
    void action(MessageReceivedEvent e) {
        e.getMessage().getChannel().sendMessage('''
Oh you want to know more about me? :3\n
Click here -> http://no-game-no-life.wikia.com/wiki/Shiro
''')
    }
}
