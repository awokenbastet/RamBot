package moe.lukas.shiro.voice.events

import moe.lukas.shiro.voice.MusicPlayer

class MusicPauseStateChangeEvent extends MusicPlayerEvent {
    protected boolean newState

    MusicPauseStateChangeEvent(MusicPlayer player, boolean newState) {
        super(player)
        this.newState = newState
    }
}
