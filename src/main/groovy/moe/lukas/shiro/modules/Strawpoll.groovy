package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

@ShiroMeta(
    enabled = false,
    description = "Create a poll at strawpoll.me",
    author = "sn0w",
    commands = [
        @ShiroCommand(command = "poll", usage = "<title> <option> <option> <option> ..."),
        @ShiroCommand(command = "straw", usage = "<title> <option> <option> <option> ...")
    ]
)
class Strawpoll implements IModule {
    void action(MessageReceivedEvent e) {
        IChannel channel = e.getMessage().getChannel()
        String[] command = e.getMessage().getContent().split(" ")

        // remove %command
        command = command.drop(1)

        // resolve title
        String title = command[0]
        command = command.drop(1)

        def arguments = [
            "fc152b36d41b9340b67cfff07ca8318ac": "",
            "poll-title"                       : title,
            "feada82fb4e7cf9128a90f52e8e106f59": "1",
            "f22faf3c671f322515881aaaef07644d4": "y",
            "fce659dada47b7d209a24f271e3510486": "y",
            "poll-submit"                      : "create"
        ]

        int i = 0
        command.each {
            i++
            arguments << ["options-option-${i}": it]
        }

        arguments << ["options-option-${i + 1}": ""]

        Unirest.post("http://www.strawpoll.me/")
            .fields(arguments).asStringAsync(
            new Callback<String>() {
                @Override
                void completed(HttpResponse<String> response) {
                    channel.sendMessage(response.getBody().toString())
                }

                @Override
                void failed(UnirestException ex) {
                    channel.sendMessage("Error :frowning:")
                }

                @Override
                void cancelled() {

                }
            }
        )
    }
}
