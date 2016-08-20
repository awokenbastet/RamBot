package moe.lukas.shiro.core

import sx.blah.discord.handle.impl.events.MessageReceivedEvent

interface IModule {
    void action(MessageReceivedEvent e);
}
