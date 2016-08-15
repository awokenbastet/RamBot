package moe.lukas.shiro.core

import sx.blah.discord.handle.impl.events.MessageReceivedEvent

abstract class IModule {
    void action(MessageReceivedEvent e) {}
}
