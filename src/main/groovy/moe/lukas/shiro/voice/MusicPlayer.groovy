package moe.lukas.shiro.voice

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.events.MusicFinishEvent
import moe.lukas.shiro.voice.events.MusicPauseStateChangeEvent
import moe.lukas.shiro.voice.events.MusicStartEvent
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.handle.audio.IAudioProvider
import sx.blah.discord.handle.obj.IGuild

@CompileStatic
class MusicPlayer implements IAudioProvider, Closeable {
    protected EventDispatcher eventDispatcher = null

    protected LinkedList<AudioSource> audioQueue = []
    protected AudioSource previousAudioSource = null
    protected AudioSource currentAudioSource = null
    protected AudioStream currentAudioStream = null

    protected State state = State.STOPPED
    protected boolean autoContinue = true

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
        byte[] frame = currentAudioStream.readFrame()

        if (frame == null) {
            sourceFinished()
            return new byte[0]
        } else {
            return frame
        }
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

        loadFromSource(audioQueue.removeFirst())

        if (state == State.STOPPED) {
            eventDispatcher.dispatch(new MusicFinishEvent(this, currentAudioSource, null))
            return
        }

        play(true)
    }

    protected void reload(boolean autoPlay, boolean fireEvent = true) {
        if (previousAudioSource == null && currentAudioSource == null) {
            throw new IllegalStateException("Cannot restart or reload a player that has never been started!")
        }

        stop(fireEvent)
        loadFromSource(previousAudioSource)

        if (autoPlay) {
            play(fireEvent)
        }
    }

    protected void sourceFinished() {
        if (currentAudioStream != null) {
            currentAudioStream.close()
        }

        currentAudioSource = null
        currentAudioStream = null

        if (autoContinue) {
            playNext(true)
        } else {
            stop(true)
        }
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

        if (paused || stopped) {
            play(true)
        }
    }
}
