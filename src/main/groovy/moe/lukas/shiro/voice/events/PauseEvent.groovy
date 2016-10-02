package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class PauseEvent extends PlayerEvent {
    PauseEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
