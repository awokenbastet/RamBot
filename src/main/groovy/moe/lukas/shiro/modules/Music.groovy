package moe.lukas.shiro.modules

import java.util.concurrent.atomic.AtomicBoolean
import com.github.axet.vget.VGet
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.VideoLoader
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.audio.AudioPlayer

@ShiroMeta(
    enabled = false,
    description = "Listen to YouTube or SoundCloud :)",
    commands = [
        @ShiroCommand(command = "join"),
        @ShiroCommand(command = "leave"),

        @ShiroCommand(command = "play"),
        @ShiroCommand(command = "stop"),
        @ShiroCommand(command = "skip"),

        @ShiroCommand(command = "add"),
        @ShiroCommand(command = "list"),
    ]
)
class Music implements IAdvancedModule {
    @Override
    void init(IDiscordClient client) {

    }

    @Override
    void action(MessageReceivedEvent e) {
        IMessage message = e.message
        IChannel channel = message.channel
        IVoiceChannel vc = message.author.connectedVoiceChannels[0]
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(channel.guild)

        player.setVolume(0.5F)

        if (channel.private) {
            channel.sendMessage("That doesn't work in PM's! :grimacing:")
        } else {
            switch (message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")) {
                case "join":
                    if (!vc.isConnected()) {
                        IMessage status = channel.sendMessage("Connecting...")
                        vc.join()
                        status.edit("~~Connecting...~~ Joined! :smiley:")
                    }
                    break

                case "leave":
                    if (vc.isConnected()) {
                        channel.sendMessage("Ok. Bye :wave:")
                        vc.leave()
                    }
                    break

                case "play":
                    player.setPaused(false)
                    break

                case "stop":
                    player.setPaused(true)
                    break

                case "skip":
                    player.skip()
                    break

                case "add":
                    String url = message.content.split(" ")[1]
                    VideoLoader.download(url, channel)
                    break
            }
        }
    }
}
