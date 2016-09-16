package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
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
    enabled = true,
    description = "Reminds you to do stuff!",
    commands = [
        @ShiroCommand(command = "remind", usage = "<your_text> <in> [number]<S|M|H|D>"),
        @ShiroCommand(command = "rm", usage = "[Alias for !remind]"),

        @ShiroCommand(command = "reminders", usage = "Lists all reminders"),
        @ShiroCommand(command = "rms", usage = "[Alias for !reminders]")
    ]
)
@CompileStatic
class Reminders implements IAdvancedModule {
    @Override
    void init(IDiscordClient client) {
        Timer.setInterval(10000, {
            ReminderList reminders = getReminders()

            reminders.each { Long ts, ReminderListEntry v ->
                if (ts <= System.currentTimeSeconds()) {
                    v.each {
                        client.getGuildByID(it.guild).getChannelByID(it.channel).sendMessage("""
Hey <@${it.user}> :smiley:
You wanted me to remind you to `${it.message}`, so DO IT NOOOW!
""")
                    }

                    reminders.remove(ts)
                }
            }

            Brain.instance.set("reminders", reminders)
        })
    }

    @Override
    void action(MessageReceivedEvent e) {
        IMessage message = e.message
        IUser user = message.author
        IChannel channel = message.channel

        if (channel.private) {
            channel.sendMessage("Doesn't work in private channels. Sorry :frowning:")
            return
        }

        switch (e.message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")) {
            case "remind":
            case "rm":
                String[] parts = message.content.split(" ")

                if (parts.size() > 4) {
                    String text
                    String time

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

                            setReminder(
                                channel.guild.ID,
                                channel.ID,
                                user.ID,
                                text,
                                ts
                            )

                            channel.sendMessage("Ok, I'll remind you :ok_hand:")
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
                IUser author = message.author
                ReminderList reminders = getReminders()

                String m = "```"

                reminders.each { Long ts, ReminderListEntry list ->
                    list.each {
                        if (it.user == author.ID) {
                            m += "${new Date(ts * 1000L)} \t - \t ${it.text}"
                        }
                    }
                }

                if (m == "```") {
                    m = "You don't have any active reminders :frowning:"
                }

                channel.sendMessage(m)
                break
        }
    }

    private ReminderList getReminders() {
        return Brain.instance.get("reminders", new ReminderList()) as ReminderList
    }

    private void setReminder(String guild, String channel, String user, String message, long time) {
        ReminderList reminders = getReminders()

        if (reminders[time] == null) {
            reminders[time] = new ReminderListEntry()
        }

        reminders[time] << [
            guild  : guild,
            channel: channel,
            user   : user,
            message: message
        ]

        Brain.instance.set("reminders", reminders)
    }

    class ReminderList extends HashMap<Long, ReminderListEntry> {
        ReminderList() { super() }
    }

    class ReminderListEntry extends ArrayList<HashMap<String, String>> {
        ReminderListEntry() { super() }
    }
}


