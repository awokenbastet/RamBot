package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = true,
    description = "Displays the weather from wttr.in",
    commands = [
        @ShiroCommand(command = "wttr", usage = "<city|moon>"),
        @ShiroCommand(command = "weather", usage = "<city|moon>")
    ]
)
@CompileStatic
class Weather implements IModule {
    private static String ENDPOINT = "http://wttr.in/"

    void action(MessageReceivedEvent e) {
        HttpResponse<String> stringResponse = null
        IChannel channel = e.message.channel
        String city = e.message.content.split(" ").drop(1).join("+")

        Core.whileTyping(channel, {
            stringResponse = Unirest
                .get(ENDPOINT + city)
                .headers(["User-Agent": "Curl"])
                .asString()
        })

        if (stringResponse.status == 200 && stringResponse.body != "ERROR") {
            if (city.toLowerCase() == "moon") {
                channel.sendMessage("Dont pretend you'd want to know .-.")
            } else {
                String response = stringResponse.body
                String[] tmp = response.split("\n")
                response = tmp.dropRight(tmp.size() - 7).join("\n")

                channel.sendMessage("```${response.replaceAll(/\[.*?m/, "")}```")
            }
        } else {
            channel.sendMessage("Error :frowning:")
        }
    }
}
