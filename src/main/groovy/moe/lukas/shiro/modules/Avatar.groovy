package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "Get a user's avatar in big",
    commands = [
        @ShiroCommand(command = "avatar", usage = "<@mention>")
    ]
)
class Avatar implements IModule {
    void action(MessageReceivedEvent e) {
        if (e.message.mentions.size() > 0) {
            e.message.channel.sendMessage(
                "There you go! :grin: \n " +
                    e.message.mentions[0].avatarURL
            )
        } else {
            e.message.channel.sendMessage("You need to @mention someone!")
        }
    }
}
