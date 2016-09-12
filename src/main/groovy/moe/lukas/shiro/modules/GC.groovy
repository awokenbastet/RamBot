package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    hidden = true,
    commands = [@ShiroCommand(command = "gc", adminOnly = true)]
)
@CompileStatic
class GC implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        System.gc()
        e.message.channel.sendMessage(":wastebasket: Done!")
    }
}
