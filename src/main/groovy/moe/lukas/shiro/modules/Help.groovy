package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.core.ModuleLoader
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    commands = [
        @ShiroCommand(command = "help"),
        @ShiroCommand(command = "h")
    ]
)
class Help implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        Core.cctv(e)

        String message = "```\n"

        ModuleLoader.modules.each { LinkedHashMap module ->
            LinkedHashMap properties = module.properties

            if (properties.enabled && properties.commands.size() > 0) {
                message += "${module.name} "

                if (properties.description == "") {
                    message += "[no description]"
                } else {
                    message += "[${properties.description}]"
                }

                message += "\n"

                properties.commands.each { ShiroCommand it ->
                    message += "\t ${Core.getPrefixForServer(e)}${it.command()} "

                    if (it.usage() != "") {
                        message += "${it.usage()}"
                    }

                    message += "\n"
                }

                message += "\n"
            }
        }

        message += "```"

        e.message.channel.sendMessage(message)
    }
}
