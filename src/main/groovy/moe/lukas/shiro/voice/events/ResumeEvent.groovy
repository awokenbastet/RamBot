package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class ResumeEvent extends PlayerEvent {
    ResumeEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
