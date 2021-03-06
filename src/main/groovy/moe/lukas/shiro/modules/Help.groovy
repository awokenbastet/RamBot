package moe.lukas.shiro.modules

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
        @ShiroCommand(command = "help"),
        @ShiroCommand(command = "h")
    ]
)
class Help implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        Core.cctv(e)

        List<String> messages = []

        String pre = """
Hi ${e.message.author.name} :smiley:
You requested to see the help for me at `${e.message.guild.name}`.
Remember to use the `${Core.getPrefixForServer(e)}` prefix in your guild :wink:\n
"""
        String message = ""

        ModuleLoader.modules.each { HashMap module ->
            HashMap properties = module.properties

            if (!properties.hidden && properties.enabled && properties.commands.size() > 0) {
                message += "${module.name}"

                if (properties.description == "") {
                    message += " [no description]"
                } else {
                    message += " [${properties.description}]"
                }

                message += "\n"

                properties.commands.each { ShiroCommand it ->
                    if (!it.hidden()) {
                        message += "\t ${Core.getPrefixForServer(e)}${it.command()} "

                        if (it.usage() != "") {
                            message += "${it.usage()}"
                        }

                        if(it.adminOnly()) {
                            message += " [ADMIN ONLY]"
                        }

                        message += "\n"
                    }
                }

                message += "\n"
            }

            if (message.size() > 1500) {
                messages << message
                message = ""
            }
        }

        if (messages.size() == 0) {
            messages << message
        }

        e.message.reply(":mailbox_with_mail:")
        e.message.author.getOrCreatePMChannel().sendMessage(pre)

        messages.each {
            e.message.author.getOrCreatePMChannel().sendMessage("```\n$it\n```")
            Thread.sleep(500)
        }
    }
}
