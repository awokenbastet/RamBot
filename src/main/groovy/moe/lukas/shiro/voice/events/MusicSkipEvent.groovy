package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer

@CompileStatic
class MusicSkipEvent extends MusicStartEvent {
    MusicSkipEvent(MusicPlayer player, AudioSource source) {
        super(player, source)
    }
}
