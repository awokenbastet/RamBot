package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

@ShiroMeta(
    enabled = true,
    description = "Test my reflexes c:",
    commands = [@ShiroCommand(command = "ping")]
)
@CompileStatic
class Ping implements IModule {
    void action(MessageReceivedEvent e) {
        long start = System.nanoTime()
        IMessage message = e.message.channel.sendMessage(":ping_pong: Pong! :grin:")
        message.edit(message.content + " (${(System.nanoTime() - start) / 1000000}ms RTT)")
    }
}
