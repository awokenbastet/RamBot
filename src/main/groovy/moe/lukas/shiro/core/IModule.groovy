package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@CompileStatic
interface IModule {
    void action(MessageReceivedEvent e);
}
