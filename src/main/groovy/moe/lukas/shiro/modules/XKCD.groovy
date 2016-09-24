package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import org.json.JSONObject
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = true,
    description = "Get the latest XKCD comic",
    commands = [
        @ShiroCommand(command = "xkcd")
    ]
)
@CompileStatic
class XKCD implements IModule {
    void action(MessageReceivedEvent e) {
        IChannel channel = e.message.channel

        HttpResponse<JsonNode> response = Unirest.get("https://xkcd.com/info.0.json").asJson()
        if (response.status != 200) {
            channel.sendMessage("Error :frowning:")
            return
        }

        JSONObject object = response.body.object
        String msg = ""

        msg += "${object.getInt("num")} from ${object.getString("day")}/${object.getString("month")}/${object.getString("year")}\n"
        msg += object.getString("title") + "\n"
        msg += object.getString("img") + "\n"
        msg += object.getString("alt")

        channel.sendMessage(msg)
    }
}
