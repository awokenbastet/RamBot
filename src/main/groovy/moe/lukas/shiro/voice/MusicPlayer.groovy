package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import sx.blah.discord.handle.audio.IAudioProvider
import sx.blah.discord.handle.audio.impl.AudioManager

@CompileStatic
class MusicPlayer extends AbstractMusicPlayer implements IAudioProvider {
    static final int PCM_FRAME_SIZE = 4
    private byte[] buffer = new byte[AudioManager.OPUS_FRAME_SIZE * PCM_FRAME_SIZE]
    private byte[] noData = new byte[0]

    @Override
    boolean isReady() {
        return state == State.PLAYING
    }

    @Override
    byte[] provide() {
        try {
            int amountRead = currentAudioStream.read(buffer, 0, buffer.length)
            if (amountRead > -1) {
                if (amountRead < buffer.length) {
                    Arrays.fill(buffer, amountRead, buffer.length - 1, (byte) 0)
                }

                if (volume != 1) {
                    short sample
                    for (int i = 0; i < buffer.length; i += 2) {
                        sample = (short) ((buffer[i + 1] & 0xff) | (buffer[i] << 8))
                        sample = (short) (sample * volume)
                        buffer[i + 1] = (byte) (sample & 0xff)
                        buffer[i] = (byte) ((sample >> 8) & 0xff)
                    }
                }

                return buffer
            } else {
                sourceFinished()
                return noData
            }
        }
        catch (IOException e) {
            e.printStackTrace()
            sourceFinished()
        }

        return noData
    }
}
