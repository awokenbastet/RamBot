package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import moe.lukas.shiro.util.Database
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

@ShiroMeta(
    enabled = true,
    description = "Rate users up/down :)",
    commands = [
        @ShiroCommand(command = "rate", usage = "<up|++|down|--> <@mention>")
    ]
)
class Ratings implements IModule {
    @Override
    void action(MessageReceivedEvent e) {
        String[] parts = e.message.content.split(" ")

        if (parts.size() == 3) {
            switch (parts[1]) {
                case "up":
                case "++":
                case "down":
                case "--":
                    int score = rate(e.message.mentions[0], e.message.guild, parts[1])
                    e.message.channel.sendMessage("""
`${e.message.mentions[0].name}` has been rated **${parts[1]}**.
His score is now `$score`
""")
                    break

                default:
                    e.message.channel.sendMessage(":no_entry: You can only vote `up/down/++/--`")
                    break
            }

        }
    }

    int rate(IUser user, IGuild guild, String direction) {
        int score = 0

        List q = Database.instance.query("SELECT `score` FROM `ratings` WHERE `user` = ? AND `guild` = ?", [
            user.ID, guild.ID
        ])

        if (q[0]?.score == null) {
            Database.instance.query("INSERT INTO `ratings` (`user`, `guild`, `score`) VALUES (?,?,?);", [
                user.ID,
                guild.ID,
                0
            ])
        } else {
            score = q[0]["score"] as int
        }

        switch (direction) {
            case "up":
            case "++":
                score++
                break

            case "down":
            case "--":
                score--
                break
        }

        Database.instance.query("UPDATE `ratings` SET `score`=? WHERE `user`=? AND `guild`=?;", [
            score,
            user.ID,
            guild.ID
        ])

        return score
    }
}
