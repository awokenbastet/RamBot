package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class NextEvent extends PlayerEvent {

    NextEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
