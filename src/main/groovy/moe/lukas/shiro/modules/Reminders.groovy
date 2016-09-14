package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

@ShiroMeta(
    enabled = false,
    description = "Reminds you to do stuff!",
    commands = [
        @ShiroCommand(command = "remind", usage = "<your_text> <in> [number]<S|M|H|D>"),
        @ShiroCommand(command = "rm", usage = "[Alias for !remind]"),

        @ShiroCommand(command = "reminders", usage = "Lists all reminders"),
        @ShiroCommand(command = "rms", usage = "[Alias for !reminders]")
    ]
)
class Reminders implements IAdvancedModule {
    @Override
    void init(IDiscordClient client) {
        Timer.setInterval(10000, {

        })
    }

    @Override
    void action(MessageReceivedEvent e) {
        IMessage message = e.message
        IUser user = message.author
        IChannel channel = message.channel

        switch (e.message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")) {
            case "remind":
            case "rm":
                String[] parts = message.content.split(" ")

                if (parts.size() > 4) {
                    String text = null
                    String time = null

                    // drop command
                    parts = parts.drop(1)

                    // add time
                    time = parts[parts.size() - 1]

                    // add text
                    parts = parts.dropRight(2)
                    text = parts.join(" ")

                    switch (time) {
                        case ~/(?i)\d+(s|m|h|d)/:
                            String unit = time.find(~/[a-z]+/)

                            long t = time.find(~/\d+/) as long
                            long ts = System.currentTimeSeconds()

                            switch (unit) {
                                case "s":
                                    ts += t
                                    break

                                case "m":
                                    ts += t * 60
                                    break

                                case "h":
                                    ts += t * 60 * 60
                                    break

                                case "d":
                                    ts += t * 60 * 60 * 24
                                    break
                            }

                            Map<Long, String> reminders = Brain.instance.get("reminders.${channel.ID}.${user.ID}", [:])
                            reminders[ts] = text
                            Brain.instance.set("reminders.${channel.ID}.${user.ID}", reminders)

                            channel.sendMessage(
                                "Ok, I'll remind you to `$text` at `${new Date(ts * 1000)}` :ok_hand:"
                            )
                            break

                        default:
                            channel.sendMessage(":x: Please check if the time-format is correct!")
                            break
                    }

                } else {
                    channel.sendMessage(":x: Please check if the format is correct!")
                }

                break

            case "reminders":
            case "rms":
                Map<Long, String> reminders = Brain.instance.get("reminders.${channel.ID}.${user.ID}", [:])

                String m = ""

                if (reminders.size() > 0) {
                    m += "Here are your pending reminders! :smiley:\n```\n"

                    reminders.each {
                        m += "${new Date(it.key * 1000)}\t:\t${it.value}\n"
                    }

                    m += "```"
                } else {
                    m += "You don't have any pending reminders right now :frowning:"
                }

                channel.sendMessage(m)
                break
        }
    }
}
