package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class ReloadEvent extends PlayerEvent {
    ReloadEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
