package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import groovy.transform.CompileStatic
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
@CompileStatic
class Minecraft implements IModule {
    void action(MessageReceivedEvent e) {
        String[] parts = e.message.content.split(" ")

        if (parts.size() < 2) {
            e.message.channel.sendMessage("Error :frowning:")
            return
        }

        HttpResponse<InputStream> stream = Unirest.get("https://minotar.net/body/${parts[1]}/300.png").asBinary()

        if (stream.status == 200) {
            e.message.channel.sendFile(stream.body, "avatar.png")
            System.gc()
            return
        }

        e.message.channel.sendMessage("Error :frowning:")
    }
}
