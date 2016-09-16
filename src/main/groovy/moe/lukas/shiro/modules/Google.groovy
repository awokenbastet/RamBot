package moe.lukas.shiro.modules

import com.mashape.unirest.http.Unirest
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.util.URLShortener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "If someone is too dumb/lazy to use google, use this.",
    commands = [
        @ShiroCommand(command = "google", usage = "<anything>"),
        @ShiroCommand(command = "g", usage = "<anything>"),
    ]
)
class Google implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        e.message.channel.sendMessage(
            ":mag: ${URLShortener.shorten("http://lmgtfy.com/?q=${e.message.content.split(" ").drop(1).join("+")}")}"
        )
    }
}
