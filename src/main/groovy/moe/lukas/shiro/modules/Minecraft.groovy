package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "Get a minecraft avatar",
    commands = [
        @ShiroCommand(command = "mc", usage = "<user>"),
        @ShiroCommand(command = "minecraft", usage = "<user>")
    ]
)
class Minecraft implements IModule {
    void action(MessageReceivedEvent e) {
        String[] parts = e.getMessage().getContent().split(" ")

        if (parts.size() < 2) {
            e.getMessage().getChannel().sendMessage("Error :frowning:")
            return
        }

        HttpResponse<InputStream> stream = Unirest.get("https://minotar.net/body/${parts[1]}/300.png").asBinary()

        if (stream.getStatus() == 200) {
            e.getMessage().getChannel().sendFile(stream.getBody(), "avatar.png")
            System.gc()
            return
        }

        e.getMessage().getChannel().sendMessage("Error :frowning:")
    }
}
