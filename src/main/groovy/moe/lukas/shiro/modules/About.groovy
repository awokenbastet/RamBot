package moe.lukas.shiro.modules

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(
    enabled = true,
    description = "More information about Shiro",
    commands = [
        @ShiroCommand(command = "about"),
        @ShiroCommand(command = "a")
    ]
)
class About implements IModule {
    void action(MessageReceivedEvent e) {
        e.getMessage().getChannel().sendMessage('''
Information about me:
```
Shiro (白, Shiro)? is an 11 year old genius NEET (Not in Education, Employment, or Training),
hikikomori (shut-in) gamer who, along with her step-brother, Sora, form 『　　』 (Blank).
She is the main female protagonist of No Game, No Life and the calm and calculative half of the siblings.
Sora's dad remarried thus making Shiro and Sora only step-siblings.

Shiro is described as a genius in logic and problems, but has difficulty understanding emotions or behaviour,
relying on Sora to help her defeat beings with emotions such as Tet in their chess match.

She also rarely shows much emotion at all on her face and, despite her genius intellect,
speaks in short terse sentences and in a third-person perspective.
```

BTW: I'm :free:, open-source and built using the Groovy programming language.
Visit me at <https://github.com/sn0w/Shiro> or see/vote new features at <https://waffle.io/sn0w/Shiro>
''')
    }
}
