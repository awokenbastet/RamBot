package moe.lukas.shiro.plugins

import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IMessage

@ShiroMeta(
        enabled = true,
        description = "More information about this bot",
        author = "sn0w"
)
class About extends IModule {
    def commands = [about: null]

    void action(IMessage e, IDiscordClient client) {

    }
}
