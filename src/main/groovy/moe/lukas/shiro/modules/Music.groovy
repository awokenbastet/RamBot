package moe.lukas.shiro.modules

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
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
import moe.lukas.shiro.voice.events.MusicStartEvent
import org.apache.commons.lang3.ObjectUtils
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.audio.IAudioManager
import sx.blah.discord.handle.audio.impl.DefaultProvider
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IVoiceChannel

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Experimental musicbot
 */
@ShiroMeta(
    enabled = false,
    description = '''
    Listen to Music :) [EXPERIMENTAL V2 | NOW WITH NATIVE OPUS SUPPORT!]
    For a list of supported hosters visit https://rg3.github.io/youtube-dl/supportedsites.html
''',
    commands = [
        @ShiroCommand(command = "join", usage = "Make the bot join your voice channel"),
        @ShiroCommand(command = "leave", usage = "Make the bot leave your voice channel"),

        @ShiroCommand(command = "play", usage = "Play the current playlist"),
        @ShiroCommand(command = "pause", usage = "Pause the playlist"),
        @ShiroCommand(command = "skip", usage = "Skip the current track"),
        @ShiroCommand(command = "clear", usage = "Clears the playlist", adminOnly = true),

        @ShiroCommand(command = "add", usage = "<url> Add a youtube link you want to play"),
        @ShiroCommand(command = "list", usage = "Show the playlist"),

        @ShiroCommand(command = "random", usage = "Adds up to 5 random songs from the bot's cache :)"),

        @ShiroCommand(command = "playing", usage = "Shows the current song"),
        @ShiroCommand(command = "np", usage = "Alias for playing")
    ]
)
@CompileStatic
class Music implements IAdvancedModule {
    private boolean acceptCommands = false

    private HashMap<String, IChannel> playerChannels = [:]

    private HashMap<String, List<String>> cliCommands = [
        download: [
            "youtube-dl",
            "--abort-on-error",
            "--no-color",
            "--no-playlist",
            "--max-filesize",
            "512m",
            "--write-info-json",
            "-f",
            "[height<=480][abr<=192][ext=mp4]",
            "-o",
            "{filename}.%(ext)s",
            "{url}"
        ],

        demux   : [
            "ffmpeg",
            "-i",
            "{input}",
            "-vn",
            "-c:a",
            "copy",
            "{output}"
        ],

        resample: [
            "ffmpeg",
            "-i",
            "{input}",
            "-ar",
            "48000",
            "{output}"
        ],

        unpack  : [
            "ffmpeg",
            "-i",
            "{input}",
            "-f",
            "s16le",
            "-acodec",
            "pcm_s16le",
            "{output}"
        ],

        repack  : [
            "opusenc",
            "--bitrate",
            "96",
            "--framesize",
            "20",
            "--padding",
            "0",
            "--discard-comments",
            "--discard-pictures",
            "--raw-rate",
            "48000",
            "--raw-endianness",
            "0",
            "--raw",
            "{input}",
            "{output}"
        ]
    ]

    @Override
    void init(IDiscordClient client) {
        println("\n[Music] Checking for youtube-dl, ffmpeg, ffprobe and opusenc...")

        boolean foundYTD = false
        boolean foundFFPROBE = false
        boolean foundFFMPEG = false
        boolean foundOpusenc = false

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

                    case ~/opusenc.*/:
                        foundOpusenc = true
                        break
                }
            }
        }

        if (foundYTD && foundFFMPEG && foundFFPROBE && foundOpusenc) {
            println("[Music] Found! Ready to load music!")
            acceptCommands = true

            EventDispatcher eventDispatcher = client.dispatcher
            eventDispatcher.registerListener(this)
        } else {
            println('[Music] Please make sure youtube-dl, ffmpeg, ffprobe and opusenc are installed and present in $PATH')
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

                        case "add":
                            String url = message.content.split(" ")[1]

                            Logger.info("Downloading $url for ${message.author.name}#${message.author.discriminator} in ${channel.name} of ${channel.guild.name}")

                            File cache = new File("cache")
                            if (!cache.exists()) {
                                cache.mkdir()
                            }

                            IMessage status = channel.sendMessage(":arrows_counterclockwise: Preparing...")

                            Timer.setTimeout(500, {
                                processURL(url, status, { boolean error, File file, boolean cached ->
                                    if (!error) {
                                        Logger.info("Success!")
                                        status.edit(":white_check_mark: Added! (${cached ? "From Cache" : "From Web"})")

                                        if (!cached) {
                                            storeTrackMeta(
                                                file.path.replace(".opus", ""),
                                                url,
                                                "${message.author.name}#${message.author.discriminator}",
                                                channel.name,
                                                channel.guild.name
                                            )
                                        }

                                        player.add(file)
                                    } else {
                                        status.edit("Error :frowning:")
                                    }
                                })
                            })
                            break

                        case "list":
                            String msg = ":musical_note: Current Playlist :musical_note: \n"

                            int i = 1
                            player.audioQueue.each { AudioSource source ->
                                msg += "$i. **${resolveTrackMeta(source.asFile().name)}**\n"
                                i++
                            }

                            channel.sendMessage(msg)
                            break

                        case "random":
                            File cache = new File("cache")

                            int counter = 0
                            cache.listFiles().any { File f ->
                                if (f.name.contains(".opus")) {
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

                        case "np":
                        case "playing":
                            channel.sendMessage(":musical_note: Currently playing: **" + resolveTrackMeta(player.currentAudioSource.asFile().name) + "**")
                            break
                    }
                } else if (command == "join") {
                    IMessage status = channel.sendMessage("Connecting...")

                    if (vc == null) {
                        status.edit("You have to join a channel first! :neutral_face:")
                    } else if (!vc.isConnected()) {
                        vc.join()

                        playerChannels[channel.guild.ID] = channel

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
    void onTrackStart(MusicStartEvent e) {
        playerChannels[e.player.guild.ID].sendMessage(
            ":musical_note: Now Playing: **${resolveTrackMeta(e.player.currentAudioSource.asFile().name)}**"
        )
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private String resolveTrackMeta(String filename) {
        filename = filename.replaceAll(~/cache(\\|\/)/, "").replace(".opus", "")

        def result = Database.instance.query(
            "SELECT `title` FROM `music` WHERE `hash` = '${filename}'"
        )[0]

        if (result == null) {
            return filename + " (meta missing)"
        } else {
            result["title"]
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void storeTrackMeta(String filename, String url, String author, String channel, String guild) {
        filename = filename.replaceAll(~/cache(\\|\/)/, "").replace(".opus", "")
        File meta = new File("cache/" + filename + ".info.json")

        def insert = [
            filename, //Hash
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
            "INSERT IGNORE INTO `music` (`hash`, `title`, `source`, `extractor`, `user`, `channel`, `guild`) VALUES (?, ?, ?, ?, ?, ?, ?);",
            insert
        )
    }

    /**
     * Process this url and apply all cli commands
     * @param url
     * @param status
     * @param callback
     */
    private void processURL(String url, IMessage status, Closure callback) {
        String cacheName = "cache/" + Core.hash(url)
        File cacheFile = new File(cacheName + ".opus")

        if (cacheFile.exists()) {
            callback(false, cacheFile, true)
        } else {
            List<LinkedHashMap<String, Object>> tasks = [
                [status : ":arrows_counterclockwise: Downloading...",
                 command: fillCommandList(cliCommands.download, [
                     "{filename}": cacheName,
                     "{url}"     : url
                 ])],
                [status : ':package: Demuxing...',
                 command: fillCommandList(cliCommands.demux, [
                     "{input}" : cacheName + ".mp4",
                     "{output}": cacheName + "_demux.m4a"
                 ])],
                [status : ':recycle: Resampling...',
                 command: fillCommandList(cliCommands.resample, [
                     "{input}" : cacheName + "_demux.m4a",
                     "{output}": cacheName + "_resample.m4a"
                 ])],
                [status : ':package: Unpacking to PCM...',
                 command: fillCommandList(cliCommands.unpack, [
                     "{input}" : cacheName + "_resample.m4a",
                     "{output}": cacheName + ".pcm"
                 ])],
                [status : ':package: Re-Packing to OPUS...',
                 command: fillCommandList(cliCommands.repack, [
                     "{input}" : cacheName + ".pcm",
                     "{output}": cacheName + ".opus"
                 ])]
            ]

            try {
                tasks.forEach({
                    status.edit(it["status"] as String)
                    spawn(it["command"] as List<String>)
                })

                [
                    "${cacheName}.mp4",
                    "${cacheName}_demux.m4a",
                    "${cacheName}_resample.m4a",
                    "${cacheName}.pcm",
                ].each { new File(it).delete() }

                callback(false, cacheFile, false)
            } catch (TimeoutException | RuntimeException e) {
                callback(true, null, false)
            }
        }
    }

    /**
     * Spawn this process
     * @param command
     * @param callback
     * @return
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    private void spawn(List<String> command) {
        Process p = new ProcessBuilder(command).start()

        String output = ""
        new Thread({
            InputStream is = p.inputStream
            InputStreamReader isr = new InputStreamReader(is)
            BufferedReader br = new BufferedReader(isr)

            String line

            while ((line = br.readLine()) != null) {
                println(line)
                output += line + "\n"
                Thread.sleep(500)
            }

            br.close()
            isr.close()
            is.close()
        }).start()

        if (p.waitFor(5, TimeUnit.MINUTES)) {
            if (p.exitValue() == 0) {
                // Normal exit (success)
            } else {
                throw new RuntimeException()
            }
        } else {
            throw new TimeoutException()
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private List<String> fillCommandList(List<String> commands, Map<String, String> replacements) {
        List<String> commandList = ObjectUtils.clone(commands)

        commandList.each { String cmd ->
            replacements.any {
                if (cmd.contains(it.key)) {
                    commandList.set(
                        commandList.indexOf(cmd),
                        cmd.replace(it.key, it.value)
                    )
                    return true
                } else {
                    return false
                }
            }
        }

        return commandList
    }

}
