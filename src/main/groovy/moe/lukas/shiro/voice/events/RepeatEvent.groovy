package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class RepeatEvent extends PlayerEvent {
    RepeatEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
