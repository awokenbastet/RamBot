package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class StopEvent extends PlayerEvent {
    StopEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
