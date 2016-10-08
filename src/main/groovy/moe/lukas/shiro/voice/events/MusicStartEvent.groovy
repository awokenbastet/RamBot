package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer

@CompileStatic
class MusicStartEvent extends MusicPlayerEvent {
    protected AudioSource source

    MusicStartEvent(MusicPlayer player, AudioSource source) {
        super(player)
        this.source = source
    }

    AudioSource getSource() {
        return source
    }
}
