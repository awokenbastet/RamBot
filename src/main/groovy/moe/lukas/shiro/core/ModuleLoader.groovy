package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMeta
import moe.lukas.shiro.annotations.ShiroMetaParser
import moe.lukas.shiro.util.Logger
import org.reflections.Reflections
import sx.blah.discord.api.IDiscordClient

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
        return new Reflections("moe.lukas.shiro.modules").getTypesAnnotatedWith(ShiroMeta.class);
    }

    /**
     * Reload all plugins
     * @param client
     */
    static void reload(IDiscordClient client) {
        modules = []
        load(client)
    }

    /**
     * Load all modules into $instances
     */
    static void load(IDiscordClient client) {
        Logger.info("Loading modules...")
        println()

        loadModules().each { Class<?> c ->
            def m = [
                name      : c.getName().replace("moe.lukas.shiro.modules.", ""),
                properties: ShiroMetaParser.parse(c),
                class     : c.newInstance()
            ]

            if (m.properties.enabled) {
                print("${m.name} reacts to [|")
                m.properties.commands.each { ShiroCommand it ->
                    print(it.command() + "|")
                }
                print("]")

                try {
                    m.class.invokeMethod("init", client)
                    print(" | Module initialized!")
                } catch (MissingMethodException e) {
                } finally {
                    print("\n")
                }

                modules << m
            } else {
                println("${m.name} is disabled!")
            }
        }

        println()
        Logger.info("Done!")
    }
}
