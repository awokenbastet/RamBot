package moe.lukas.shiro.core

import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMetaParser
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
    static ArrayList<Class> loadModules() {
        ArrayList<Class> mods = []

        File shiroModules = new File("shiro_modules")
        if (!shiroModules.exists()) {
            shiroModules.mkdir()
        } else if (shiroModules.isFile()) {
            shiroModules.delete()
            shiroModules.mkdir()
        }

        GroovyClassLoader classLoader = new GroovyClassLoader()
        shiroModules.listFiles().each {
            mods << classLoader.parseClass(new File(it.getPath()))
        }

        return mods
    }

    /**
     * Load all modules into $instances
     */
    static void load(IDiscordClient client) {
        Logger.info("Loading modules...")
        println()

        loadModules().each { Class c ->
            def m = [
                name      : c.getName(),
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
