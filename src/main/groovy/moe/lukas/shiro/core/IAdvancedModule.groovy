package moe.lukas.shiro.core

import sx.blah.discord.api.IDiscordClient

interface IAdvancedModule extends IModule {
    void init(IDiscordClient client);
}
