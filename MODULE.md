## Simple Modules

```groovy
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.core.IModule

@ShiroMeta(
    enabled = true, 
    description = "", 
    author = "sn0w",
    commands = [
        //would result in "!hello <world>" on !help
        @ShiroCommand(command = "hello", usage = "world")
        // a command without usage information
        @ShiroCommand(command = "hw")
    ]
)
class MyModuleName implements IModule {
    void action(MessageReceivedEvent e) {
        // your awesome plugin logic \o/
    }
}
```
All properties of the `@ShiroMeta()` annotation are optional.<br>
The default values are:
```
enabled     => false
description => ""
author      => "anonymous"
commands    => []
```

You are free to do add **any** methods and properties you want to your class, but you **must** implement `void action(e)`.

As Shiro is a genius you don't need to tell her how you named your plugin.<br>
She will guess it from your class name using reflection magic.

## Advanced Modules
Advanced modules work exactly like normal modules but provide a fake-constructor

```groovy
import sx.blah.discord.api.IDiscordClient
import moe.lukas.shiro.core.IAdvancedModule
import moe.lukas.shiro.annotations.ShiroMeta
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

@ShiroMeta(enabled = true, author = "sn0w", ...)
class Something implements IAdvancedModule {
    void init(IDiscordClient client) {
        // initialize your module (runs once at load)
    }

    void action(MessageReceivedEvent e) {
        // respond to a user command
    }
}

```
