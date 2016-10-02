package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class FinishEvent extends PlayerEvent {
    FinishEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
