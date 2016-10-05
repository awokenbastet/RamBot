package moe.lukas.shiro.voice

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.events.MusicFinishEvent
import moe.lukas.shiro.voice.events.MusicPauseStateChangeEvent
import moe.lukas.shiro.voice.events.MusicStartEvent
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.audio.IAudioProvider
import sx.blah.discord.handle.audio.impl.AudioManager
import sx.blah.discord.handle.obj.IGuild

import java.nio.BufferUnderflowException

@CompileStatic
class MusicPlayer implements IAudioProvider, Closeable {
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

    protected IGuild guild

    protected enum State {
        PLAYING, PAUSED, STOPPED
    }

    MusicPlayer(EventDispatcher dispatcher, IGuild guild = null) {
        eventDispatcher = dispatcher
        this.guild = guild
    }

    @Override
    boolean isReady() {
        return state == State.PLAYING
    }

    @Override
    IAudioProvider.AudioEncodingType getAudioEncodingType() {
        return IAudioProvider.AudioEncodingType.OPUS
    }

    @Override
    @CompileDynamic
    byte[] provide() {
        try {
            return currentAudioStream.readFrame(AudioManager.OPUS_FRAME_SIZE)
        } catch (BufferUnderflowException e) {
            e.printStackTrace()
            sourceFinished()
            currentAudioStream.close()
        }

        return new byte[0]
    }

    @Override
    void close() {
        currentAudioStream.close()
    }

    protected void loadFromSource(AudioSource source) {
        AudioStream stream = source.asStream()
        currentAudioSource = source
        currentAudioStream = stream
    }

    void skipToNext() {
        AudioSource skipped = currentAudioSource
        playNext(true)
    }

    void play(boolean fireEvent = true) {
        if (state == State.PLAYING) {
            return
        }

        if (currentAudioSource != null) {
            state = State.PLAYING
            return
        }

        if (audioQueue.isEmpty()) {
            throw new IllegalStateException("MusicPlayer: The audio queue is empty! Cannot start playing.")
        }

        loadFromSource(audioQueue.removeFirst())
        state = State.PLAYING

        if (fireEvent) {
            eventDispatcher.dispatch(new MusicStartEvent(this, currentAudioSource))
        }
    }

    void pause() {
        if (state == State.PAUSED) {
            return
        }

        if (state == State.STOPPED) {
            throw new IllegalStateException("Cannot pause a stopped player!")
        }

        state = State.PAUSED

        eventDispatcher.dispatch(new MusicPauseStateChangeEvent(this, state == State.PAUSED))
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

    void stop(boolean fireEvent = true) {
        if (state == State.STOPPED) {
            return
        }

        state = State.STOPPED

        try {
            currentAudioStream.close()
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            previousAudioSource = currentAudioSource
            currentAudioSource = null
            currentAudioStream = null
        }

        if (fireEvent) {
            eventDispatcher.dispatch(new MusicPauseStateChangeEvent(this, true))
        }
    }

    void clear() {
        stop(true)
        audioQueue = []
    }

    protected void playNext(boolean fireEvent) {
        stop(false)

        if (audioQueue.isEmpty()) {
            if (fireEvent) {
                eventDispatcher.dispatch(new MusicFinishEvent(this, currentAudioSource, null))
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

        if (state == State.STOPPED) {
            eventDispatcher.dispatch(new MusicFinishEvent(this, currentAudioSource, null))
            return
        }

        eventDispatcher.dispatch(new MusicFinishEvent(this, currentAudioSource, audioQueue.get(0)))
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
                eventDispatcher.dispatch(new MusicFinishEvent(this, currentAudioSource, currentAudioSource))
            } else {
                playNext(true)
            }
        } else {
            stop(true)
        }
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

    IGuild getGuild() {
        return guild
    }

    void add(File f) {
        AudioSource source = new AudioSource(f)

        audioQueue << source

        if (stopped) {
            play(true)
        }
    }
}
