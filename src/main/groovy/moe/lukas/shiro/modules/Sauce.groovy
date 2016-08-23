package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.util.URLShortener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = false,
    author = "sn0w",
    description = "Do a reverse image search",
    commands = [
        @ShiroCommand(command = "src", usage = "<url>"),
        @ShiroCommand(command = "sauce", usage = "<url>")
    ]
)
class Sauce implements IModule {
    void action(MessageReceivedEvent e) {
        IChannel channel = e.getMessage().getChannel()
        String url = ""

        Core.whileTyping(channel, {
            url = URLShortener.shorten(
                "https://images.google.com/searchbyimage?image_url=${e.getMessage().getContent().split(" ")[1]}" +
                    "&encoded_image=&image_content=&filename=&hl=en-US"
            )
        })

        e.getMessage().getChannel().sendMessage(
            url == null ? "Error :frowning:" : url
        )
    }
}
