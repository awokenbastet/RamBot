package moe.lukas.shiro.voice

import moe.lukas.shiro.voice.events.*

abstract class PlayerListenerAdapter implements PlayerEventListener {

    void onPlay(PlayEvent event) {}

    void onPause(PauseEvent event) {}

    void onResume(ResumeEvent event) {}

    void onStop(StopEvent event) {}

    void onSkip(SkipEvent event) {}

    void onFinish(FinishEvent event) {}

    void onRepeat(RepeatEvent event) {}

    void onReload(ReloadEvent event) {}

    void onNext(NextEvent event) {}

    @Override
    void onEvent(PlayerEvent event) {
        if (event instanceof PlayEvent)
            onPlay((PlayEvent) event)
        else if (event instanceof PauseEvent)
            onPause((PauseEvent) event)
        else if (event instanceof ResumeEvent)
            onResume((ResumeEvent) event)
        else if (event instanceof StopEvent)
            onStop((StopEvent) event)
        else if (event instanceof SkipEvent)
            onSkip((SkipEvent) event)
        else if (event instanceof FinishEvent)
            onFinish((FinishEvent) event)
        else if (event instanceof RepeatEvent)
            onRepeat((RepeatEvent) event)
        else if (event instanceof ReloadEvent)
            onReload((ReloadEvent) event)
        else if (event instanceof NextEvent)
            onNext((NextEvent) event)
    }
}
