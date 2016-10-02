package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer
import moe.lukas.shiro.voice.AudioSource

@CompileStatic
class SkipEvent extends PlayerEvent {
    protected final AudioSource skippedSource

    SkipEvent(AbstractMusicPlayer player, AudioSource skippedSource) {
        super(player)
        this.skippedSource = skippedSource
    }

    AudioSource getSkippedSource() {
        return skippedSource
    }
}
