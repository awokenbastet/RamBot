package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.util.URLShortener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = true,
    author = "sn0w",
    description = "Shorten a URL",
    commands = [
        @ShiroCommand(command = "shorten", usage = "<url>"),
        @ShiroCommand(command = "shrt", usage = "<url>")
    ]
)
class Shorten implements IModule {
    void action(MessageReceivedEvent e) {
        IChannel channel = e.getMessage().getChannel()
        String url = ""

        Core.whileTyping(channel, {
            url = URLShortener.shorten(e.getMessage().getContent().split(" ")[1])
        })

        e.getMessage().getChannel().sendMessage(
            url == null ? "Error :frowning:" : url
        )
    }
}
