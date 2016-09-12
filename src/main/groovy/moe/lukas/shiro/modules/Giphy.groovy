package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import org.json.JSONArray
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = true,
    description = "Search for gifs",
    commands = [
        @ShiroCommand(command = "gif", usage = "<query>"),
        @ShiroCommand(command = "giphy", usage = "<query>")
    ]
)
@CompileStatic
class Giphy implements IModule {
    private static String ENDPOINT = "http://api.giphy.com/v1/gifs/search?"
    private static String API_KEY = "dc6zaTOxFJmzC"
    private static String RATING = "pg-13"
    private static int LIMIT = 5

    void action(MessageReceivedEvent e) {
        HttpResponse<JsonNode> jsonResponse = null;
        IChannel channel = e.getMessage().getChannel()
        String query = e.getMessage().getContent().split(" ").drop(1).join("+")

        Core.whileTyping(channel, {
            jsonResponse = Unirest.get(
                "${ENDPOINT}q=${query}&api_key=${API_KEY}&rating=${RATING}&limit=${LIMIT}"
            ).asJson()
        })

        if (jsonResponse.getStatus() != 200) {
            channel.sendMessage("Search failed (Error ${jsonResponse.getStatus()}) :frowning:")
        } else {
            JsonNode body = jsonResponse.getBody()
            JSONArray data = body.getObject().getJSONArray("data")

            if (data.size() > 0) {
                channel.sendMessage(data.getJSONObject(new Random().nextInt(data.size())).getString("bitly_url"))
            } else {
                channel.sendMessage("No gifs found :frowning:")
            }
        }
    }
}
