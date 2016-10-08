package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "Get an invite link for me!",
    commands = [
        @ShiroCommand(command = "invite")
    ]
)
class Invite implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        e.message.channel.sendMessage("""
To invite me click this link :smiley:
https://discordapp.com/oauth2/authorize?client_id=214185976375803904&scope=bot&permissions=104188928
""")
    }
}
