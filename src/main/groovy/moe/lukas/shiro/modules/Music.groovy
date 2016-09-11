package moe.lukas.shiro.modules

import java.util.concurrent.TimeUnit
import groovy.json.JsonSlurper
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.util.Timer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.audio.AudioPlayer
import sx.blah.discord.util.audio.events.TrackStartEvent

/**
 * Experimental musicbot
 */
@ShiroMeta(
    enabled = true,
    description = '''
    Listen to Music :) [EXPERIMENTAL]
    For a list of supported hosters visit https://rg3.github.io/youtube-dl/supportedsites.html
''',
    commands = [
        @ShiroCommand(command = "join", usage = "Make the bot join your voice channel"),
        @ShiroCommand(command = "leave", usage = "Make the bot leave your voice channel"),

        @ShiroCommand(command = "play", usage = "Play the current playlist"),
        @ShiroCommand(command = "pause", usage = "Pause the playlist"),
        @ShiroCommand(command = "skip", usage = "Skip the current track"),
        @ShiroCommand(command = "clear", usage = "Clears the playlist [ADMIN ONLY]", adminOnly = true),
        @ShiroCommand(command = "loop", usage = "Toggle looping the playlist [ADMIN ONLY]", adminOnly = true),
        @ShiroCommand(command = "shuffle", usage = "Shuffle the playlist [ADMIN ONLY]", adminOnly = true),

        @ShiroCommand(command = "add", usage = "<url> Add a youtube link you want to play"),
        @ShiroCommand(command = "list", usage = "Show the playlist"),

        @ShiroCommand(command = "vol", usage = "Change the volume [ADMIN ONLY]", adminOnly = true)
    ]
)
class Music implements IAdvancedModule {
    private boolean acceptCommands = false

    private HashMap<String, IChannel> playerChannels = [:]

    @Override
    void init(IDiscordClient client) {
        println("\n[Music] Checking for youtube-dl and ffmpeg...")

        boolean foundYTD = false

        System.getenv("PATH").split(File.pathSeparator).each {
            new File(it).listFiles().each {
                switch (it.name) {
                    case "youtube-dl":
                    case "youtube-dl.exe":
                        foundYTD = true
                        break
                }
            }
        }

        if (foundYTD) {
            println("[Music] Found! Ready to load music!")
            acceptCommands = true

            EventDispatcher eventDispatcher = client.getDispatcher()
            eventDispatcher.registerListener(this)
        } else {
            println('[Music] Please install ffmpeg/libav and youtube-dl and add it to your $PATH or %PATH%')
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

            if (channel.private) {
                channel.sendMessage("That doesn't work in PM's! :grimacing:")
            } else {
                if (vc.connectedUsers.contains(message.author)) {
                    switch (message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")) {
                        case "join":
                            if (!vc.isConnected()) {
                                IMessage status = channel.sendMessage("Connecting...")
                                vc.join()
                                status.edit("Joined! :smiley:")

                                playerChannels[channel.guild.ID] = channel
                            }
                            break

                        case "leave":
                            if (vc.isConnected()) {
                                channel.sendMessage("OK, bye :wave:")
                                vc.leave()

                                playerChannels.remove(channel.guild.ID)
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

                        case "clear":

                            player.clear()
                            channel.sendMessage(":wastebasket: Cleared!")
                            break

                        case "loop":
                            player.setLoop(!player.looping)
                            channel.sendMessage(":repeat: Looping ${player.looping ? "en" : "dis"}abled!")

                            break

                        case "shuffle":
                            player.shuffle()
                            channel.sendMessage(":twisted_rightwards_arrows: Shuffled all tracks!")
                            break

                        case "add":
                            String url = message.content.split(" ")[1]

                            Logger.info("Downloading $url for ${message.author.name}#${message.author.discriminator} in ${channel.name} of ${channel.guild.name}")

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
                                        "--abort-on-error",
                                        "--no-color",
                                        "--no-playlist",
                                        "--max-filesize",
                                        "128m",
                                        "--prefer-avconv",
                                        "--write-info-json",
                                        "--add-metadata",
                                        "-x",
                                        "-f",
                                        "bestaudio/best",
                                        "--audio-format",
                                        "mp3",
                                        "-o",
                                        "$cacheName.%(ext)s",
                                        url
                                    ).start()

                                    // Log YTDL in other thread
                                    // This thread will block until YTDL is complete
                                    String output = ""
                                    new Thread({
                                        InputStream is = ytdl.getInputStream()
                                        InputStreamReader isr = new InputStreamReader(is)
                                        BufferedReader br = new BufferedReader(isr)

                                        String line

                                        while ((line = br.readLine()) != null) {
                                            println(line)
                                            output += line + "\n"
                                        }

                                        br.close()
                                        isr.close()
                                        is.close()
                                    }).start()

                                    // Wait for end of YTDL execution
                                    if (ytdl.waitFor(5, TimeUnit.MINUTES)) {
                                        if (ytdl.exitValue() == 0) {
                                            Logger.info("Success!")
                                            status.edit(":white_check_mark: Added! (Downloaded)")
                                            player.queue(new File(cacheName + ".mp3"))
                                        } else {
                                            Logger.err("Error!")
                                            status.edit("Error :frowning: \n```\n$output\n```")
                                        }
                                    } else {
                                        Logger.err("YTDL Timeout")
                                        status.edit(":no_entry: Timeout (Waited for 5 minutes). \n Please try again. (maybe a shorter video?)")
                                        new File("cache").listFiles().each {
                                            if (it.name.matches(/${Core.hash(url)}.*/)) {
                                                it.delete()
                                            }
                                        }
                                    }
                                }
                            })
                            break

                        case "list":
                            String msg = ":musical_note: Current Playlist :musical_note: \n"

                            int i = 1
                            player.playlist.each {
                                msg += "**$i.** " + resolveTrackMeta((it.metadata["file"] as File).name)
                                i++
                            }

                            channel.sendMessage(msg)
                            System.gc()
                            break

                        case "vol":
                            float vol = message.content.split(" ")[1] as float

                            player.setVolume(vol)
                            channel.sendMessage(":speaker: **$vol%**")
                            break
                    }
                }
            }
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @EventSubscriber
    void onTrackStart(TrackStartEvent e) {
        playerChannels[e.player.guild.ID].sendMessage(
            ":musical_note: Now Playing: **${resolveTrackMeta((e.track.metadata["file"] as File).name)}**"
        )
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String resolveTrackMeta(String filename) {
        File meta = new File("cache/" + filename.replace(".mp3", ".info.json"))

        if (meta.exists()) {
            def json = new JsonSlurper().parse(meta.readBytes())

            return "${json.title}\n"
        } else {
            return "${filename} _(META missing)_\n"
        }
    }
}
