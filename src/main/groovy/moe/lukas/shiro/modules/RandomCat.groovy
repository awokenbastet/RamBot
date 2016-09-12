package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import groovy.transform.CompileStatic
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "Random cat images",
    commands = [
        @ShiroCommand(command = "cat")
    ]
)
@CompileStatic
class RandomCat implements IModule {
    void action(MessageReceivedEvent e) {
        HttpResponse<JsonNode> response = Unirest.get("http://random.cat/meow").asJson()
        if (response.getStatus() != 200) {
            e.getMessage().getChannel().sendMessage("Error :crying_cat_face:")
            return
        }

        e.getMessage().getChannel().sendMessage(
            "MEOW! :smiley_cat: \n " +
                response.getBody().getObject().getString("file")
        )
    }
}
