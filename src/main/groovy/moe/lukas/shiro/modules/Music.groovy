package moe.lukas.shiro.modules

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.util.Database
import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.util.Timer
import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.audio.IAudioManager
import sx.blah.discord.handle.audio.impl.DefaultProvider
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IVoiceChannel
import sx.blah.discord.util.RateLimitException
import sx.blah.discord.util.audio.events.TrackStartEvent

import java.util.concurrent.TimeUnit

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
        @ShiroCommand(command = "clear", usage = "Clears the playlist", adminOnly = true),
        @ShiroCommand(command = "loop", usage = "Toggle looping the playlist", adminOnly = true),
        @ShiroCommand(command = "shuffle", usage = "Shuffle the playlist", adminOnly = true),

        @ShiroCommand(command = "add", usage = "<url> Add a youtube link you want to play"),
        @ShiroCommand(command = "list", usage = "Show the playlist"),

        @ShiroCommand(command = "vol", usage = "Change the volume", adminOnly = true),

        @ShiroCommand(command = "random", usage = "Adds up to 5 random songs from the bot's cache :)"),
    ]
)
@CompileStatic
class Music implements IAdvancedModule {
    private boolean acceptCommands = false

    private HashMap<String, IChannel> playerChannels = [:]

    @Override
    void init(IDiscordClient client) {
        println("\n[Music] Checking for youtube-dl, ffmpeg and ffprobe...")

        boolean foundYTD = false
        boolean foundFFPROBE = false
        boolean foundFFMPEG = false

        System.getenv("PATH").split(File.pathSeparator).each { String f ->
            new File(f).listFiles().each { File ff ->
                switch (ff.name) {
                    case ~/youtube-dl.*/:
                        foundYTD = true
                        break

                    case ~/ffprobe.*/:
                        foundFFPROBE = true
                        break

                    case ~/ffmpeg.*/:
                        foundFFMPEG = true
                        break
                }
            }
        }

        if (foundYTD && foundFFMPEG && foundFFPROBE) {
            println("[Music] Found! Ready to load music!")
            acceptCommands = true

            EventDispatcher eventDispatcher = client.dispatcher
            eventDispatcher.registerListener(this)
        } else {
            println('[Music] Please make sure ffmpeg, ffprobe and youtube-dl are installed and present in $PATH')
            println('[Music] This plugin will disable itself to prevent errors!')
        }
    }

    @Override
    void action(MessageReceivedEvent e) {
        if (acceptCommands) {
            IMessage message = e.message
            IChannel channel = message.channel
            IVoiceChannel vc = message.author.connectedVoiceChannels[0]
            IAudioManager audioManager = vc.guild.audioManager
            MusicPlayer player

            if (audioManager.audioProvider instanceof DefaultProvider) {
                player = new MusicPlayer(e.client.dispatcher, channel.guild)
                player.setVolume(0.1F)
                audioManager.setAudioProvider(player)
            } else {
                player = audioManager.audioProvider as MusicPlayer
            }

            if (channel.private) {
                channel.sendMessage("That doesn't work in PM's! :grimacing:")
            } else {
                String command = message.content.split(" ")[0].replace(Core.getPrefixForServer(e), "")

                if (vc?.connectedUsers?.contains(e.client.ourUser)) {
                    switch (command) {
                        case "leave":
                            if (vc.isConnected()) {
                                channel.sendMessage("OK, bye :wave:")
                                vc.leave()
                                player.clear()

                                playerChannels.remove(channel.guild.ID)
                            }
                            break

                        case "play":
                            if (!player.playing) {
                                player.play(true)
                            }
                            break

                        case "pause":
                            if (player.playing) {
                                player.pause()
                            }
                            break

                        case "skip":
                            player.skipToNext()
                            break

                        case "clear":
                            player.clear()
                            channel.sendMessage(":wastebasket: Cleared!")
                            break

                        case "shuffle":
                            player.setShuffle(!player.shuffle)
                            channel.sendMessage(
                                player.shuffle ?
                                    ":twisted_rightwards_arrows: Shuffle enabled!" :
                                    ":arrow_forward: Shuffle disabled!"
                            )
                            break

                        case "add":
                            String url = message.content.split(" ")[1]

                            Logger.info("Downloading $url for ${message.author.name}#${message.author.discriminator} in ${channel.name} of ${channel.guild.name}")

                            File cache = new File("cache")
                            if (!cache.exists()) {
                                cache.mkdir()
                            }

                            String downloading = ":arrows_counterclockwise: Downloading..."
                            IMessage status = channel.sendMessage(downloading)

                            Timer.setTimeout(500, {
                                String cacheName = "cache/" + Core.hash(url)

                                if (new File(cacheName + ".mp3").exists()) {
                                    status.edit(":white_check_mark: Added! (from cache)")
                                    player.add(new File(cacheName + ".mp3"))
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
                                        InputStream is = ytdl.inputStream
                                        InputStreamReader isr = new InputStreamReader(is)
                                        BufferedReader br = new BufferedReader(isr)

                                        String line = "Preparing..."
                                        String lastLine = ""

                                        Thread interval = Timer.setInterval(2000, {
                                            try {
                                                if(line != lastLine) {
                                                    status.edit(downloading + "\n```\n$line\n```")
                                                }

                                                lastLine = line
                                            } catch(RateLimitException ex) {
                                            }
                                        })

                                        while ((line = br.readLine()) != null) {
                                            println(line)
                                            output += line + "\n"
                                            Thread.sleep(500)
                                        }

                                        br.close()
                                        isr.close()
                                        is.close()
                                        interval.stop()
                                    }).start()

                                    // Wait for end of YTDL execution
                                    if (ytdl.waitFor(5, TimeUnit.MINUTES)) {
                                        if (ytdl.exitValue() == 0) {
                                            Logger.info("Success!")
                                            status.edit(":white_check_mark: Added! (Downloaded)")
                                            player.add(new File(cacheName + ".mp3"))

                                            storeTrackMeta(
                                                cacheName,
                                                url,
                                                "${message.author.name}#${message.author.discriminator}",
                                                channel.name,
                                                channel.guild.name
                                            )
                                        } else {
                                            Logger.err("Error!")
                                            status.edit("Error :frowning: \n```\n$output\n```")
                                        }
                                    } else {
                                        Logger.err("YTDL Timeout")
                                        status.edit(":no_entry: Timeout (Waited for 5 minutes). \n Please try again. (maybe a shorter video?)")
                                        new File("cache").listFiles().each { File f ->
                                            if (f.name.matches(/${Core.hash(url)}.*/)) {
                                                f.delete()
                                            }
                                        }
                                    }
                                }
                            })
                            break

                        case "list":
                            String msg = ":musical_note: Current Playlist :musical_note: \n"

                            int i = 1
                            player.audioQueue.each { AudioSource source ->
                                msg += "**$i.** " + resolveTrackMeta(source.asFile().name) + "\n"
                                i++
                            }

                            channel.sendMessage(msg)
                            break

                        case "vol":
                            if (message.content.split(" ").size() == 1) {
                                channel.sendMessage(":speaker: **${Math.round(player.volume * 100)}%**")
                            } else {
                                float vol = ((message.content.split(" ")[1] as float) / 100) as float

                                player.setVolume(vol)
                                channel.sendMessage(":speaker: **${Math.round(player.volume * 100)}%**")
                            }
                            break

                        case "random":
                            File cache = new File("cache")

                            int counter = 0
                            cache.listFiles().any { File f ->
                                if (f.name.contains(".mp3")) {
                                    if (counter >= 5) {
                                        return true
                                    }

                                    if (!player.audioQueue.any { it.asFile().name == f.name }) {
                                        player.add(f)
                                        counter++
                                    }
                                }
                            }

                            channel.sendMessage("Done :smiley:")
                            break
                    }
                } else if (command == "join") {
                    IMessage status = channel.sendMessage("Connecting...")

                    if (vc == null) {
                        status.edit("You have to join a channel first! :neutral_face:")
                    } else if (!vc.isConnected()) {
                        vc.join()

                        playerChannels[channel.guild.ID] = channel

                        player.setVolume(0.1F)

                        status.edit("Joined! :smiley:")
                    } else {
                        status.edit("Already joined :neutral_face:")
                    }
                } else {
                    channel.sendMessage(":no_entry: You need to join the Voice-Chat that I'm in or use `${Core.getPrefixForServer(e)}join`.")
                }
            }
        }
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @EventSubscriber
    void onTrackStart(TrackStartEvent e) {
        playerChannels[(e.player as MusicPlayer).guild.ID].sendMessage(
            ":musical_note: Now Playing: **${resolveTrackMeta((e.player as MusicPlayer).currentAudioSource.asFile().name)}**"
        )
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String resolveTrackMeta(String filename) {
        filename = filename.replace("cache/", "").replace(".mp3", "")

        def result = Database.instance.query(
            "SELECT `title` FROM `music` WHERE `hash` = '${filename}'"
        )[0]

        if (result == null) {
            return filename
        } else {
            result["title"]
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void storeTrackMeta(String filename, String url, String author, String channel, String guild) {
        File meta = new File(filename + ".info.json")

        def insert = [
            filename.replace("cache/", ""), //Hash
            null, // title
            url,  // source
            null, //extractor
            author,
            channel,
            guild
        ]

        if (meta.exists()) {
            def json = new JsonSlurper().parse(meta.readBytes())
            insert[1] = json["title"] as String
            insert[3] = json["extractor"] as String
        }

        Database.instance.query(
            "INSERT INTO `music` (`hash`, `title`, `source`, `extractor`, `user`, `channel`, `guild`) VALUES (?, ?, ?, ?, ?, ?, ?);",
            insert
        )
    }
}
