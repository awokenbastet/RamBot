package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import moe.lukas.shiro.voice.events.PlayerEvent

@CompileStatic
class PlayerEventManager {
    private final List<PlayerEventListener> listeners = new LinkedList<>()

    PlayerEventManager() {}

    void register(PlayerEventListener listener) {
        if (listeners.contains(listener))
            throw new IllegalArgumentException("Attempted to register a listener that is already registered")
        listeners.add(listener)
    }

    void unregister(PlayerEventListener listener) {
        listeners.remove(listener)
    }

    void handle(PlayerEvent event) {
        List<PlayerEventListener> listenerCopy = new LinkedList<>(listeners)
        for (PlayerEventListener listener : listenerCopy) {
            try {
                listener.onEvent(event)
            }
            catch (Throwable throwable) {
                throwable.printStackTrace()
            }
        }
    }

    List<PlayerEventListener> getListeners() {
        return Collections.unmodifiableList(listeners)
    }
}
