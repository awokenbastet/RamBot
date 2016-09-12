package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "Stone someone to death!!!1!11!",
    commands = [
        @ShiroCommand(command = "stone", usage = "<@mention>")
    ]
)
@CompileStatic
class Stone implements IModule {
    void action(MessageReceivedEvent e) {
        String id = e.getMessage().getMentions()[0].ID
        e.getMessage().getChannel().sendMessage("<@${id}>" + ''' IS GOING TO DIE!!!
COME ON GUYS. THROW SOME STONES WITH MEE!!!
:grimacing: :wavy_dash::anger::astonished:
''')
    }
}

