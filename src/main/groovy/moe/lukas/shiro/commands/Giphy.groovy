package moe.lukas.shiro.commands

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
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
    author = "sn0w",
    commands = [
        @ShiroCommand(command = "gif", usage = "<query>"),
        @ShiroCommand(command = "giphy", usage = "<query>")
    ]
)
class Giphy implements IModule {
    private static String ENDPOINT = "http://api.giphy.com/v1/gifs/search?"
    private static String API_KEY = "dc6zaTOxFJmzC"
    private static String RATING = "pg-13"
    private static int LIMIT = 5

    void action(MessageReceivedEvent e) {
        IChannel channel = e.getMessage().getChannel()
        String query = e.getMessage().getContent().split(" ").drop(1).join("+")

        Core.enableTyping(channel)

        HttpResponse<JsonNode> jsonResponse = Unirest.get(
            "${ENDPOINT}q=${query}&api_key=${API_KEY}&rating=${RATING}&limit=${LIMIT}"
        ).asJson()

        Core.disableTyping(channel)

        if(jsonResponse.getStatus() != 200) {
            channel.sendMessage("Search failed (Error ${jsonResponse.getStatus()}) :frowning:")
        } else {
            JsonNode body = jsonResponse.getBody()
            JSONArray data = body.getObject().getJSONArray("data")

            if(data.size() > 0) {
                channel.sendMessage(data.getJSONObject(new Random().nextInt(data.size())).getString("bitly_url"))
            } else {
                channel.sendMessage("No gifs found :frowning:")
            }
        }
    }
}
