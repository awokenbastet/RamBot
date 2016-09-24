package moe.lukas.shiro.modules

import groovy.transform.CompileStatic

import java.util.concurrent.ThreadLocalRandom
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = true,
    description = "Roll a random number",
    commands = [
        @ShiroCommand(command = "roll", usage = "<min> <max>")
    ]
)
@CompileStatic
class Roll implements IModule {
    void action(MessageReceivedEvent e) {
        IChannel channel = e.message.channel
        String[] parts = e.message.content.split(" ")

        if (parts.size() == 3) {
            channel.sendMessage(
                ":crystal_ball: " +
                    ThreadLocalRandom.current().nextInt(parts[1].toInteger(), parts[2].toInteger() + 1)
            )
        } else {
            channel.sendMessage("Seems like you made a typo :frowning:")
        }
    }
}
