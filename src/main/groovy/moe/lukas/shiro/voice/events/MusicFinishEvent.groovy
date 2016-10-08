package moe.lukas.shiro.voice.events

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.AudioSource
import moe.lukas.shiro.voice.MusicPlayer

@CompileStatic
class MusicFinishEvent extends MusicPlayerEvent {
    protected AudioSource oldTrack
    protected AudioSource newTrack

    MusicFinishEvent(MusicPlayer player, AudioSource oldTrack, AudioSource newTrack) {
        super(player)
        this.oldTrack = oldTrack
        this.newTrack = newTrack
    }

    AudioSource getOldTrack() { return oldTrack }

    AudioSource getNewTrack() { return newTrack }
}
