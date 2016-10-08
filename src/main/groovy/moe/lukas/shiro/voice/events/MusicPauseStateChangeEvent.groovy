package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.MusicPlayer

@CompileStatic
class MusicPauseStateChangeEvent extends MusicPlayerEvent {
    protected boolean newState

    MusicPauseStateChangeEvent(MusicPlayer player, boolean newState) {
        super(player)
        this.newState = newState
    }
}
