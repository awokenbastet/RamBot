package moe.lukas.shiro.modules

import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.util.SystemInfo
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser

@ShiroMeta(
    enabled = true,
    description = "Show the bot's statistics",
    commands = [@ShiroCommand(command = "stats")]
)
@CompileStatic
class Stats implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        IMessage msg = e.message.channel.sendMessage(":arrows_counterclockwise: Collecting information...")

        Timer.setTimeout(500, {
            // Process information
            Runtime runtime = Runtime.getRuntime();

            // Discord information
            IDiscordClient client = e.client
            IUser bot = client.ourUser

            int humans = 0
            int bots = 0
            int combined = 0
            e.message.guild.users.each { it.bot ? bots++ : humans++; combined++ }

            int servers = 0
            int channels = 0
            int users = 0
            client.guilds.each {
                servers++
                users += it.users.size()
                it.channels.each { channels++ }
            }

            msg.edit("""
Hi, I'm Shiro!
Here are some stats about me :smiley:

```
--------------------- System Information ---------------------
OS:       ${SystemInfo.OS}
Process:  ${SystemInfo.process}
JVM:      ${SystemInfo.JVM}
JVM Spec: ${SystemInfo.spec}
Uptime:   ${SystemInfo.uptime}

--------------------- Process Information --------------------
Allocated RAM:      ${SystemInfo.allocatedRam}
Used allocated RAM: ${SystemInfo.usedAllocatedRam}
Free allocated RAM: ${SystemInfo.freeAllocatedRam}
Max. usable RAM:    ${SystemInfo.maxUsableRam}

--------------------- Discord Information --------------------
Connected Servers:        $servers
Watching Channels:        $channels
Connected voice-channels: ${bot.connectedVoiceChannels.size()}
Users with access to me:  $users

----------------------- Bot Information ----------------------
My Nickname: ${bot.name}#${bot.discriminator} (${bot.getNicknameForGuild(e.message.guild)})
Status:      ${bot.status}
Presence:    ${bot.presence}

Framework: Discord4J (https://github.com/austinv11/Discord4J)
Language:  Groovy (http://groovy-lang.org/) and Java
```
""")
        })
    }
}
