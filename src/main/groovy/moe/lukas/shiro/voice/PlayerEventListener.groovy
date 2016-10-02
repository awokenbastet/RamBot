package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.events.PlayerEvent

@CompileStatic
interface PlayerEventListener {
    void onEvent(PlayerEvent event)
}
