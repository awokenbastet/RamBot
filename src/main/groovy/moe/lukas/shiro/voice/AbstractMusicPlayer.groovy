package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.events.*

@CompileStatic
abstract class AbstractMusicPlayer {
    protected PlayerEventManager eventManager = new PlayerEventManager()
    protected LinkedList<AudioSource> audioQueue = new LinkedList<>()
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

    void addEventListener(PlayerEventListener listener) {
        eventManager.register(listener)
    }

    void removeEventListener(PlayerEventListener listener) {
        eventManager.unregister(listener)
    }

    List<PlayerEventListener> getListeners() {
        return eventManager.getListeners()
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

    void reload(boolean autoPlay) {
        reload0(autoPlay, true)
    }

    void skipToNext() {
        AudioSource skipped = currentAudioSource
        playNext(false)

        eventManager.handle(new SkipEvent(this, skipped))
        if (state == State.STOPPED)
            eventManager.handle(new FinishEvent(this))
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

    AudioTimestamp getCurrentTimestamp() {
        if (currentAudioStream != null)
            return currentAudioStream.getCurrentTimestamp()
        else
            return null
    }

    void play() {
        play0(true)
    }

    void pause() {
        if (state == State.PAUSED)
            return

        if (state == State.STOPPED)
            throw new IllegalStateException("Cannot pause a stopped player!")

        state = State.PAUSED
        eventManager.handle(new PauseEvent(this))
    }

    void stop() {
        stop0(true)
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

    // ========= Internal Functions ==========

    protected void play0(boolean fireEvent) {
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
            eventManager.handle(new PlayEvent(this))
    }

    protected void stop0(boolean fireEvent) {
        if (state == State.STOPPED)
            return

        state = State.STOPPED
        try {
            currentAudioStream.close()
        }
        catch (IOException e) {
            e.printStackTrace()
        }
        finally {
            previousAudioSource = currentAudioSource
            currentAudioSource = null
            currentAudioStream = null
        }

        if (fireEvent)
            eventManager.handle(new StopEvent(this))
    }

    protected void reload0(boolean autoPlay, boolean fireEvent) {
        if (previousAudioSource == null && currentAudioSource == null)
            throw new IllegalStateException("Cannot restart or reload a player that has never been started!")

        stop0(false)
        loadFromSource(previousAudioSource)

        if (autoPlay)
            play0(false)
        if (fireEvent)
            eventManager.handle(new ReloadEvent(this))
    }

    protected void playNext(boolean fireEvent) {
        stop0(false)
        if (audioQueue.isEmpty()) {
            if (fireEvent)
                eventManager.handle(new FinishEvent(this))
            return
        }

        AudioSource source
        if (shuffle) {
            Random rand = new Random()
            source = audioQueue.remove(rand.nextInt(audioQueue.size()))
        } else
            source = audioQueue.removeFirst()
        loadFromSource(source)

        play0(false)
        if (fireEvent)
            eventManager.handle(new NextEvent(this))
    }

    protected void sourceFinished() {
        if (autoContinue) {
            if (repeat) {
                reload0(true, false)
                eventManager.handle(new RepeatEvent(this))
            } else {
                playNext(true)
            }
        } else
            stop0(true)
    }

    protected void loadFromSource(AudioSource source) {
        AudioStream stream = source.asStream()
        currentAudioSource = source
        currentAudioStream = stream
    }
}
