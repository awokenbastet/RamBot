package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import sx.blah.discord.api.IDiscordClient

/**
 * An advanced module that gets initialized
 *
 * After that it behaves like an IModule
 */
@CompileStatic
interface IAdvancedModule extends IModule {
    void init(IDiscordClient client);
}
