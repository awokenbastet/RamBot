package moe.lukas.shiro.voice.events

import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer

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
