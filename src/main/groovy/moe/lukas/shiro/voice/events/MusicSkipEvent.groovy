package moe.lukas.shiro.voice.events

import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer

class MusicSkipEvent extends MusicStartEvent {
    MusicSkipEvent(MusicPlayer player, AudioSource source) {
        super(player, source)
    }
}
