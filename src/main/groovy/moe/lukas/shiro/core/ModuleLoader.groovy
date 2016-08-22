package moe.lukas.shiro.core

import org.reflections.Reflections
import moe.lukas.shiro.util.Logger
import sx.blah.discord.api.IDiscordClient
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMetaParser

/**
 * Class to help loading/finding modules
 * Reflection > all ;)
 */
class ModuleLoader {
    static List<LinkedHashMap> modules = []

    /**
     * Returns all present modules
     * @return
     */
    static Set<Class<?>> loadModules() {
        return new Reflections("moe.lukas.shiro.commands").getTypesAnnotatedWith(ShiroMeta.class);
    }

    /**
     * Load all modules into $instances
     */
    static void load(IDiscordClient client) {
        Logger.info("Loading modules...")
        println()

        loadModules().each { Class<?> c ->
            def m = [
                name      : c.getName().replace("moe.lukas.shiro.commands.", ""),
                properties: ShiroMetaParser.parse(c),
                class     : c
            ]

            print("${m.name} reacts to [|")
            m.properties.commands.each { ShiroCommand it ->
                print(it.command() + "|")
            }
            print("]")

            try {
                c.newInstance().invokeMethod("init", client)
                print(" | Module initialized!")
            } catch (MissingMethodException e) {
            } finally {
                print("\n")
            }

            modules << m
        }

        println()
        Logger.info("Done!")
    }
}
