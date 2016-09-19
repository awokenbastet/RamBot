package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

/**
 * An a module that executes an action on commands
 */
@CompileStatic
interface IModule {
    void action(MessageReceivedEvent e);
}
