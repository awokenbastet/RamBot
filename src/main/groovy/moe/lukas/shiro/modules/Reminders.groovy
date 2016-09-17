package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Database
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

@CompileStatic
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
class Reminders implements IAdvancedModule {
    IDiscordClient client = null

    @Override
    void init(IDiscordClient client) {
        this.client = client

        Timer.setInterval(10000, {
            HashMap<Long, ArrayList<HashMap<String, String>>> reminders = getReminders()

            reminders.each { Long ts, ArrayList<HashMap<String, String>> v ->
                if (ts <= System.currentTimeSeconds()) {
                    v.each {
                        client.getGuildByID(it.guild).getChannelByID(it.channel).sendMessage("""
Hey <@${it.user}> :smiley:
You wanted me to remind you to `${it.message}`, so DO IT NOOOW!
""")
                        Database.instance.query("UPDATE `shiro`.`reminders` SET `sent`='1' WHERE `id` = ${it.id};")
                    }
                }
            }
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

                if (parts.size() >= 4) {
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
                HashMap<Long, ArrayList<HashMap<String, String>>> reminders = getReminders()

                String m = "```\n"

                reminders.each { Long ts, ArrayList<HashMap<String, String>> list ->
                    list.each {
                        if (it.user == author.ID) {
                            m += "${new Date(ts * 1000L)} - ${it["message"]} | "
                            m += "Target: #${client.getGuildByID(it["guild"]).getChannelByID(it["channel"]).name} in "
                            m += "${client.getGuildByID(it["guild"]).name}\n"
                        }
                    }
                }

                if (m == "```\n") {
                    m = "You don't have any active reminders :frowning:"
                } else {
                    m += "\n```"
                }

                channel.sendMessage(m)
                break
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private HashMap<Long, ArrayList<HashMap<String, String>>> getReminders(long time = 0L) {
        List<Map<String, Object>> data = Database.instance.query(
                "SELECT * FROM `reminders` WHERE `sent` = 0" +
                        (
                                time == 0L ?
                                        "" :
                                        " AND `timestamp` = `$time`"
                        )
                        + ";"
        )

        HashMap<Long, ArrayList<HashMap<String, String>>> reminders = [:]

        data.each {
            long ts = it["timestamp"] as long
            it.remove("timestamp")

            if (reminders[ts] == null) {
                reminders[ts] = new ArrayList<HashMap<String, String>>()
            }

            reminders[ts] << (it as HashMap<String, String>)
        }

        return reminders
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void setReminder(String guild, String channel, String user, String message, long time) {
        Database.instance.query(
                "INSERT INTO `shiro`.`reminders` (`timestamp`, `guild`, `channel`, `user`, `message`) VALUES (?, ?, ?, ?, ?);",
                [
                        time as String,
                        guild,
                        channel,
                        user,
                        message
                ]
        )
    }
}
