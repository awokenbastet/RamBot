package moe.lukas.shiro.core

import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IMessage

/**
 * Interface that provides a boilerplate to build modules from
 *
 * You have access to the awesome annotations for JIT dependency resolving.
 * See http://docs.groovy-lang.org/latest/html/documentation/grape.html for more information.
 *
 * All Shiro-Modules are instance-free to avoid flooding the stack with useless objects
 * Thus you need to define everything as static
 */
abstract class IModule {
    /**
     * The commands to listen for (Map<String>)
     *
     * Use this format:
     * [command: "argument information", command2: "argument information", ...]
     *
     * If you don't want to set arguments pass null or emptystring
     */
    LinkedHashMap commands

    /**
     * Closure that gets executed after your command was triggered
     *
     * @param e The IMessage object containing everything about your message
     * @param client Access to the discord client
     */
    void action(IMessage e, IDiscordClient client) {}
}
