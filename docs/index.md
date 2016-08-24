# Shiro
**A a multipurpose [Discord](https://discordapp.com/) bot written in [Groovy](http://groovy-lang.org/)**

These are the Docs for Shiro.<br>
Here you'll find literally anything you'll ever want to know.

To skip through the categories use the sidebar :)

# Writing Modules
Unlike other bots, Shiro is designed to know nothing.<br>
All functionality (except `!help`) comes from modules.<br>
That way Shiro stays extensible while keeping the core small.

To write a module you just need to create a class in the `moe.lukas.shiro.modules` package that implements either `IModule` or `IAdvancedModule`.

You **don't** need to provide a name for your modules.<br>
Shiro will guess it at runtime using reflection-magic.

### Simple Modules (`IModule`)
Simple modules are the most common ones.<br>
They define commands and react to them in a `void action(e)`

`e` is a simple `IMessageReceivedEvent` from Discord4J.

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
        @ShiroCommand(command = "hello", usage = "world"),
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

### Advanced Modules (`IAdvancedModule`)
Advanced modules provide a way to execute code **before** any command is triggered.<br>
This may be useful if you want to extend Shiro's Core or don't need to listen to commands at all.<br>

You need to define a `void init()` that takes a `IDiscordClient` instance.

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

# Annotations
Shiro uses annotations instead of class properties to store meta information.

## `@ShiroMeta()`
The ShiroMeta annotation is used to store literal meta-information.<br>
All properties are listed on the right.

Note that even though all properties are optional you need to provide **at least** an empty annotation.
Shiro rejects modules without `@ShiroMeta()` annotation.

```groovy
@ShiroMeta(
    enabled = true/false,
    description = "My awesome module",
    author = "John Doe"
    commands = [
        @ShiroCommand(...)
    ]
)
```

## `@ShiroCommand()`
This annotation is used to register command listeners.<br>
As you can see you **have to** provide the `command` property but `usage` is optional.

```groovy
[
    @ShiroCommand(command = "hello", usage = "<name>"),
    @ShiroCommand(command = "world")
]
```
