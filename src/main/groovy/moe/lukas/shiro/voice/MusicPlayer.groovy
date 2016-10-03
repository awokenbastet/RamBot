package moe.lukas.shiro.voice

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.audio.IAudioProvider
import sx.blah.discord.handle.audio.impl.AudioManager
import sx.blah.discord.util.audio.events.PauseStateChangeEvent
import sx.blah.discord.util.audio.events.TrackFinishEvent
import sx.blah.discord.util.audio.events.TrackSkipEvent
import sx.blah.discord.util.audio.events.TrackStartEvent

@CompileStatic
class MusicPlayer implements IAudioProvider {

    static final int PCM_FRAME_SIZE = 4

    private byte[] buffer = new byte[AudioManager.OPUS_FRAME_SIZE * PCM_FRAME_SIZE]
    private byte[] noData = new byte[0]

    protected EventDispatcher eventDispatcher = null

    protected LinkedList<AudioSource> audioQueue = []
    protected AudioSource previousAudioSource = null
    protected AudioSource currentAudioSource = null
    protected AudioStream currentAudioStream = null

    protected State state = State.STOPPED
    protected boolean autoContinue = true
    protected boolean shuffle = false
    protected boolean repeat = false

    protected float volume = 1.0F

    protected enum State {
        PLAYING, PAUSED, STOPPED
    }

    MusicPlayer(EventDispatcher dispatcher) {
        eventDispatcher = dispatcher
    }

    @Override
    boolean isReady() {
        return state == State.PLAYING
    }

    @Override
    @CompileDynamic
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
        } catch (IOException e) {
            e.printStackTrace()
            sourceFinished()
        }

        return noData
    }

    void skipToNext() {
        AudioSource skipped = currentAudioSource
        playNext(true)
    }

    AudioTimestamp getCurrentTimestamp() {
        if (currentAudioStream != null) {
            return currentAudioStream.getCurrentTimestamp()
        } else {
            return null
        }
    }

    void play(boolean fireEvent) {
        if (state == State.PLAYING)
            return

        if (currentAudioSource != null) {
            state = State.PLAYING
            return
        }

        if (audioQueue.isEmpty())
            throw new IllegalStateException("MusicPlayer: The audio queue is empty! Cannot start playing.")

        loadFromSource(audioQueue.removeFirst())
        state = State.PLAYING

        if (fireEvent)
            eventDispatcher.dispatch(new TrackStartEvent(null, null))
    }

    void pause() {
        if (state == State.PAUSED)
            return

        if (state == State.STOPPED)
            throw new IllegalStateException("Cannot pause a stopped player!")

        state = State.PAUSED
        eventDispatcher.dispatch(new PauseStateChangeEvent(null, state == State.PAUSED))
    }

    boolean isPlaying() {
        return state == State.PLAYING
    }

    boolean isPaused() {
        return state == State.PAUSED
    }

    boolean isStopped() {
        return state == State.STOPPED
    }

    void stop(boolean fireEvent) {
        if (state == State.STOPPED)
            return

        state = State.STOPPED

        try {
            currentAudioStream.close()
        }
        catch (IOException e) {
            e.printStackTrace()
        } finally {
            previousAudioSource = currentAudioSource
            currentAudioSource = null
            currentAudioStream = null
        }

        if (fireEvent)
            eventDispatcher.dispatch(new PauseStateChangeEvent(null, true))
    }

    void clear() {
        stop(true)
        audioQueue = []
    }

    protected void playNext(boolean fireEvent) {
        stop(false)

        if (audioQueue.isEmpty()) {
            if (fireEvent) {
                eventDispatcher.dispatch(new TrackFinishEvent(null, null, null))
            }

            return
        }

        AudioSource source

        if (shuffle) {
            Random rand = new Random()
            source = audioQueue.remove(rand.nextInt(audioQueue.size()))
        } else {
            source = audioQueue.removeFirst()
        }

        loadFromSource(source)

        eventDispatcher.dispatch(new TrackSkipEvent(null, null))

        if (state == State.STOPPED)
            eventDispatcher.dispatch(new TrackFinishEvent(null, null, null))
    }

    protected void reload(boolean autoPlay, boolean fireEvent) {
        if (previousAudioSource == null && currentAudioSource == null) {
            throw new IllegalStateException("Cannot restart or reload a player that has never been started!")
        }

        stop(false)
        loadFromSource(previousAudioSource)

        if (autoPlay) {
            play(false)
        }
    }

    protected void sourceFinished() {
        if (autoContinue) {
            if (repeat) {
                reload(true, false)
                eventDispatcher.dispatch(new TrackFinishEvent(null, null, null))
            } else {
                playNext(true)
            }
        } else {
            stop(true)
        }
    }

    protected void loadFromSource(AudioSource source) {
        AudioStream stream = source.asStream()
        currentAudioSource = source
        currentAudioStream = stream
    }

    void setRepeat(boolean repeat) {
        this.repeat = repeat
    }

    boolean isRepeat() {
        return repeat
    }

    float getVolume() {
        return this.volume
    }

    void setVolume(float volume) {
        this.volume = volume
    }

    void setShuffle(boolean shuffle) {
        this.shuffle = shuffle
    }

    boolean isShuffle() {
        return shuffle
    }

    LinkedList<AudioSource> getAudioQueue() {
        return audioQueue
    }

    AudioSource getCurrentAudioSource() {
        return currentAudioSource
    }

    AudioSource getPreviousAudioSource() {
        return previousAudioSource
    }

    void queue(File f) {
        AudioSource source = new LocalSource(f)
        AudioInfo info = source.getInfo()

        audioQueue << source

        if (stopped) {
            play(true)
        }
    }
}
