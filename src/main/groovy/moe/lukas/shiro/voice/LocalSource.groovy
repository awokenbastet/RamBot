package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import org.json.JSONException
import org.json.JSONObject
import sun.misc.IOUtils

import java.nio.file.FileAlreadyExistsException

@CompileStatic
class LocalSource implements AudioSource {
    static final List<String> FFMPEG_LAUNCH_ARGS =
        Collections.unmodifiableList(Arrays.asList(
            "ffmpeg",       //Program launch
            "-f", "s16be",  //Format.  PCM, signed, 16bit, Big Endian
            "-ac", "2",     //Channels. Specify 2 for stereo audio.
            "-ar", "48000", //Rate. Opus requires an audio rate of 48000hz
            "-map", "a",    //Makes sure to only output audio, even if the specified format supports other streams
            "-"             //Used to specify STDout as the output location (pipe)
        ))
    static final List<String> FFPROBE_INFO_ARGS =
        Collections.unmodifiableList(Arrays.asList(
            "ffprobe",
            "-show_format",
            "-print_format", "json",
            "-loglevel", "0"
        ))

    private File file
    private AudioInfo audioInfo

    LocalSource(File file) {
        if (file == null)
            throw new IllegalArgumentException("Provided file was null!")
        if (!file.exists())
            throw new IllegalArgumentException("Provided file does not exist!")
        if (file.isDirectory())
            throw new IllegalArgumentException("Provided file is actually a directory. Must provide a file!")
        if (!file.canRead())
            throw new IllegalArgumentException("Provided file is unreadable due to a lack of permissions")

        this.file = file
    }

    @Override
    String getSource() {
        try {
            return file.getCanonicalPath()
        }
        catch (IOException e) {
            e.printStackTrace()
        }
        return null
    }

    @Override
    synchronized AudioInfo getInfo() {
        if (audioInfo != null)
            return audioInfo

        audioInfo = new AudioInfo()
        try {
            List<String> infoArgs = new LinkedList<>()
            infoArgs.addAll(FFPROBE_INFO_ARGS)
            infoArgs.add("-i")
            infoArgs.add(file.getCanonicalPath())

            Process infoProcess = new ProcessBuilder().command(infoArgs).start()
            byte[] infoData = IOUtils.readFully(infoProcess.getInputStream(), -1, false)
            if (infoData == null || infoData.length == 0)
                throw new NullPointerException("The FFprobe process resulted in a null or zero-length INFO!")

            JSONObject info = new JSONObject(new String(infoData))
            JSONObject format = info.getJSONObject("format")
            JSONObject tags = format.optJSONObject("tags")

            audioInfo.jsonInfo = info
            audioInfo.origin = file.getCanonicalPath()
            audioInfo.extractor = "LocalSource"

            if (tags != null) {
                audioInfo.title = !tags.optString("title", "").isEmpty() ? tags.getString("title") : null

                audioInfo.description =
                    "Title: " + (tags.has("title") ? tags.getString("title") : "N/A") + "\n" +
                        "Artist: " + (tags.has("artist") ? tags.getString("artist") : "N/A") + "\n" +
                        "Album: " + (tags.has("album") ? tags.getString("album") : "N/A") + "\n" +
                        "Genre: " + (tags.has("genre") ? tags.getString("genre") : "N/A") + "\n"
            }

            audioInfo.encoding =
                !format.optString("format_name", "").isEmpty() ?
                    format.getString("format_name") : !format.optString("format_long_name", "").isEmpty() ?
                    format.getString("format_long_name") : null

            audioInfo.duration = format.has("duration") ?
                AudioTimestamp.fromSeconds((int) format.getDouble("duration")) : null

        }
        catch (IOException | JSONException e) {
            audioInfo.error = e.getMessage()
            e.printStackTrace()
        }
        return audioInfo
    }

    @Override
    AudioStream asStream() {
        List<String> ffmpegLaunchArgs = new LinkedList<>()
        ffmpegLaunchArgs.addAll(FFMPEG_LAUNCH_ARGS)
        try {
            ffmpegLaunchArgs.add("-i")
            ffmpegLaunchArgs.add(file.getCanonicalPath())
            return new LocalStream(ffmpegLaunchArgs)
        }
        catch (IOException e) {
            e.printStackTrace()
            return null
        }
    }

    @Override
    File asFile(String path, boolean deleteOnExists) throws FileAlreadyExistsException, FileNotFoundException {
        return null
    }

    @Override
    File asFile() {
        return this.file
    }
}
