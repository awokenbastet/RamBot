package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils

import java.nio.ByteBuffer

@CompileStatic
class AudioStream implements Closeable {
    private volatile ByteBuffer buffer

    AudioStream(File file) {
        byte[] fileBytes = FileUtils.readFileToByteArray(file)

        buffer = ByteBuffer.wrap(fileBytes)
        buffer.flip()
        buffer.rewind()
    }

    byte[] readFrame(int frameSize) {
        byte[] data = new byte[frameSize]
        buffer.get(data, buffer.position(), frameSize)

        return data
    }

    @Override
    void close() {
        buffer = null
    }
}
