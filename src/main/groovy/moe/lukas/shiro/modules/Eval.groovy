package moe.lukas.shiro.modules

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.core.IModule
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IMessage

@ShiroMeta(
    enabled = false,
    description = """"
Run arbitrary code! \\o/
Use "eval langs" to get a list of languages
""",
    commands = [
        @ShiroCommand(command = "eval", usage = "<language> <code> | langs")
    ]
)
class Eval implements IModule {
    private List<String> langs = [
        "C",
        "C++",
        "D",
        "Haskell",
        "Lua",
        "OCaml",
        "PHP",
        "Perl",
        "Python",
        "Ruby",
        "Scheme",
        "Tcl"
    ]

    @Override
    void action(MessageReceivedEvent e) {
        IChannel channel = e.message.channel
        String prefix = Core.getPrefixForServer(e)
        String[] msg = e.message.content.split(" ")

        if (msg.size() < 3) {
            String wrongFormat = "Wrong format :frowning: \n Please use something like ```\n${prefix}eval C++\n` ` `\n<code>\n` ` `\n```"

            if (msg.size() == 2) {
                switch (msg[1]) {
                    case ~/^(?i)lang.*/:
                        channel.sendMessage("```\nNOTE: Language tags *not* case-sensitive!\n${langs.join("\n")}\n```")
                        return

                    default:
                        channel.sendMessage(wrongFormat)
                        return
                }
            } else {
                channel.sendMessage(wrongFormat)
                return
            }
        }

        String command = msg[0]
        msg = msg.drop(1)

        String lang = msg[0]
        msg = msg.drop(1)

        String code = msg.join(" ").replaceAll("```", "")

        IMessage m = channel.sendMessage(":arrows_counterclockwise: Compiling stuff...")

        boolean httpError = false
        boolean matched = langs.any {
            if (it ==~ /(?i)${lang}/) {

                HttpResponse<String> response = Unirest.post("http://codepad.org/").fields([
                    lang   : it,
                    code   : code,
                    private: 'True',
                    submit : 'Submit',
                    run    : 'True'
                ]).asString()

                if (response.status == 302) {
                    HttpResponse<String> codeResponse = Unirest.get(response.headers["Location"][0]).asString()

                    if (codeResponse.status == 200) {
                        Document d = Jsoup.parse(codeResponse.body)
                        String stdout = d.select(".code").last().select("table > tbody > tr > td:nth-child(2) > div > pre").text()

                        m.edit("""
Here you go!
```
${stdout.substring(0, Math.min(1500, stdout.size()))}
```
""")
                    } else {
                        httpError = true
                    }
                } else {
                    httpError = true
                }

                return true
            }
        }

        if (!matched) {
            m.edit("Unrecognized language :frowning: \n Use `${prefix}eval langs` to get a list of languages.")
            return
        }

        if (httpError) {
            m.edit(":x: HTTP Error")
        }
    }
}
