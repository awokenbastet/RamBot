package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.audio.AudioPlayer

/**
 * Experimental musicbot
 */
@ShiroMeta(
    enabled = true,
    description = "\n Listen to Music :) [EXPERIMENTAL] \n For a list of supported hosters visit https://rg3.github.io/youtube-dl/supportedsites.html \n",
    commands = [
        @ShiroCommand(command = "join", usage = "Make the bot join your voice channel"),
        @ShiroCommand(command = "leave", usage = "Make the bot leave your voice channel"),

        @ShiroCommand(command = "play", usage = "Play the current playlist"),
        @ShiroCommand(command = "pause", usage = "Pause the playlist"),
        @ShiroCommand(command = "skip", usage = "Skip the current track"),

        @ShiroCommand(command = "add", usage = "<url> Add a youtube link you want to play"),
        @ShiroCommand(command = "list", usage = "Show the playlist"),
    ]
)
class Music implements IAdvancedModule {
    private boolean acceptCommands = false

    @Override
    void init(IDiscordClient client) {
        println("\n[Music] Checking for youtube-dl and ffmpeg...")

        boolean foundFFMPEG = false
        boolean foundYTDL = false

        System.getenv("PATH").split(File.pathSeparator).each {
            new File(it).listFiles().each {
                switch (it.name) {
                    case "youtube-dl":
                    case "youtube-dl.exe":
                        foundYTDL = true
                        break

                    case "ffmpeg":
                    case "ffmpeg.exe":
                        foundFFMPEG = true
                        break
                }
            }
        }

        if (foundYTDL && foundFFMPEG) {
            println("[Music] Found! Ready to load music!")
            acceptCommands = true
        } else {
            println('[Music] Please install ffmpeg and youtube-dl and add it to your $PATH or %PATH%')
            println('[Music] This plugin will disable itself to prevent errors!')
        }
    }

    @Override
    void action(MessageReceivedEvent e) {
        if (acceptCommands) {
            IMessage message = e.message
            IChannel channel = message.channel
            IVoiceChannel vc = message.author.connectedVoiceChannels[0]
            AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(channel.guild)

            player.setVolume(0.1F)

            if (channel.private) {
                channel.sendMessage("That doesn't work in PM's! :grimacing:")
            } else {
                switch (message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")) {
                    case "join":
                        if (!vc.isConnected()) {
                            IMessage status = channel.sendMessage("Connecting...")
                            vc.join()
                            status.edit("Joined! :smiley:")
                        }
                        break

                    case "leave":
                        if (vc.isConnected()) {
                            channel.sendMessage("OK, bye :wave:")
                            vc.leave()
                        }
                        break

                    case "play":
                        player.setPaused(false)
                        break

                    case "pause":
                        player.setPaused(true)
                        break

                    case "skip":
                        player.skip()
                        break

                    case "add":
                        String url = message.content.split(" ")[1]

                        File cache = new File("cache")
                        if (!cache.exists()) {
                            cache.mkdir()
                        }

                        IMessage status = channel.sendMessage(":arrows_counterclockwise: Downloading...")

                        Timer.setTimeout(500, {
                            String cacheName = "cache/" + Core.hash(url)

                            if (new File(cacheName + ".mp3").exists()) {
                                status.edit(":white_check_mark: Added! (from cache)")
                                player.queue(new File(cacheName + ".mp3"))
                            } else {
                                Process ytdl = new ProcessBuilder(
                                    "youtube-dl",
                                    "-x",
                                    "--audio-format",
                                    "mp3",
                                    "-o",
                                    "$cacheName.%(ext)s",
                                    url
                                ).start()

                                InputStream is = ytdl.getInputStream()
                                InputStreamReader isr = new InputStreamReader(is)
                                BufferedReader br = new BufferedReader(isr)

                                String output = ""
                                String line

                                while ((line = br.readLine()) != null) {
                                    println(line)
                                    output += line + "\n"
                                }

                                if (ytdl.exitValue() == 0) {
                                    status.edit(":white_check_mark: Added! (Downloaded)")
                                    player.queue(new File(cacheName + ".mp3"))
                                } else {
                                    status.edit("Error :frowning: \n```\n$output\n```")
                                }
                            }
                        })
                        break

                    case "list":
                        String msg = ":musical_note: Current Playlist :musical_note: \n"

                        player.playlist.each {
                            File f = it.metadata["file"] as File
                            msg += "${f.name}\n"
                        }

                        channel.sendMessage(msg)
                        break
                }
            }
        }
    }
}
