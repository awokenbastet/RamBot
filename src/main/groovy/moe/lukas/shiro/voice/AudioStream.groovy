package moe.lukas.shiro.voice

import groovy.transform.CompileStatic

import java.util.regex.Pattern

@CompileStatic
abstract class AudioStream extends BufferedInputStream {
    static final Pattern TIME_PATTERN = Pattern.compile("(?<=time=).*?(?= bitrate)")

    AudioStream() {
        super(null)
    }

    abstract AudioTimestamp getCurrentTimestamp()
}
