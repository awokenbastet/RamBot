package moe.lukas.shiro.commands

import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.annotations.ShiroCommand
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    author = "sn0w",
    description = "Stone someone to death!!!1!11!",
    commands = [
        @ShiroCommand(command = "stone", usage = "<@mention>")
    ]
)
class Stone implements IModule {
    void action(MessageReceivedEvent e) {
        String id = e.getMessage().getMentions()[0].getID()
        e.getMessage().getChannel().sendMessage("<@${id}>" + ''' IS GOING TO DIE!!!
COME ON GUYS. THROW SOME STONES WITH MEE!!!
:grimacing: :wavy_dash::anger::astonished:
''')
    }
}

