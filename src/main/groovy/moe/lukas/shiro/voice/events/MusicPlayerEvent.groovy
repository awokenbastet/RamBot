package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.MusicPlayer
import sx.blah.discord.api.events.Event

@CompileStatic
class MusicPlayerEvent extends Event {
    protected final MusicPlayer player

    public MusicPlayerEvent(MusicPlayer player) {
        this.player = player;
    }

    MusicPlayer getPlayer() {
        return player
    }
}
