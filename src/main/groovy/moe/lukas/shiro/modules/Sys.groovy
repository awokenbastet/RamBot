package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.core.ModuleLoader

import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    hidden = true,
    commands = [
        @ShiroCommand(command = "plugins:reload", adminOnly = true)
    ]
)
@CompileStatic
class Sys implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        switch (e.message.content.replace(Core.getPrefixForServer(e), "")) {
            case "plugins:reload":
                ModuleLoader.reload(e.client)
                e.message.channel.sendMessage(":cyclone: Plugins reloaded!")
                break
        }
    }
}
