package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
class PlayEvent extends PlayerEvent {

    PlayEvent(AbstractMusicPlayer player) {
        super(player)
    }
}
