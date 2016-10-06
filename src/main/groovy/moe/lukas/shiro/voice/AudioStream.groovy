package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import org.gagravarr.opus.OpusFile

@CompileStatic
class AudioStream implements Closeable {
    private volatile OpusFile opusFile

    AudioStream(File file) {
        opusFile = new OpusFile(file)
    }

    byte[] readFrame() {
        return opusFile.nextAudioPacket?.data
    }

    @Override
    void close() {
        opusFile.close()
    }
}
