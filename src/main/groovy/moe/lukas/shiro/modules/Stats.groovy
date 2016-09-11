package moe.lukas.shiro.modules

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.text.SimpleDateFormat
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IUser

@ShiroMeta(
    enabled = true,
    description = "Show the bot's statistics",
    commands = [@ShiroCommand(command = "stats")]
)
class Stats implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        // System information
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
        String uptime = sdf.format(rb.uptime)

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
        client.guilds.each {
            servers++
            it.channels.each { channels++ }
        }

        e.message.channel.sendMessage("""
Hi, I'm Shiro!
Here are some stats about me :smiley:

```
--------------------- System Information ---------------------
OS:       ${System.getProperty("os.name")} [Version: ${System.getProperty("os.version")} | Arch: ${
            System.getProperty("os.arch")
        }]
Process:  ${rb.name}
JVM:      ${rb.vmName}@${rb.vmVersion} by ${rb.vmVendor}
JVM Spec: ${rb.specName}@${rb.specVersion} by ${rb.specVendor}
Uptime:   $uptime

--------------------- Process Information --------------------
Allocated RAM:      ${Math.round(runtime.totalMemory() / 1048576)}mb
Used allocated RAM: ${Math.round((runtime.totalMemory() - runtime.freeMemory()) / 1048576)}mb
Free allocated RAM: ${Math.round(runtime.freeMemory() / 1048576)}mb
Max. usable RAM:    ${Math.round(runtime.maxMemory() / 1048576)}mb

--------------------- Discord Information --------------------
Connected Servers:        $servers
Watching Channels:        $channels
Connected voice-channels: ${bot.connectedVoiceChannels.size()}

My Nickname: ${bot.name}#${bot.discriminator} (${bot.getNicknameForGuild(e.message.guild)})
Status: ${bot.status}
Presence: ${bot.presence}
```
""")
    }
}
