package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AbstractMusicPlayer

@CompileStatic
abstract class PlayerEvent {
    protected final AbstractMusicPlayer player

    PlayerEvent(AbstractMusicPlayer player) {
        this.player = player
    }

    AbstractMusicPlayer getPlayer() {
        return player
    }
}
