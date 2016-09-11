package moe.lukas.shiro.core

import groovy.transform.CompileStatic
import sx.blah.discord.api.IDiscordClient

@CompileStatic
interface IAdvancedModule extends IModule {
    void init(IDiscordClient client);
}
