package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMetaParser
import moe.lukas.shiro.util.Logger
import org.reflections.Reflections

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
    static Set<Class<? extends IModule>> loadModules() {
        Reflections reflections = new Reflections("shiro_modules")
        return reflections.getSubTypesOf(IModule)
    }

    /**
     * Load all modules into $instances
     */
    static void load() {
        Logger.info("Loading modules...")

        loadModules().each { Class<? extends IModule> c ->
            def m = [
                name      : c.name,
                properties: ShiroMetaParser.parse(c),
                instance  : c.newInstance()
            ]

            print("${m.name} reacts to [|")
            m.properties.commands.each { ShiroCommand it ->
                print(it.command() + "|")
            }
            print("]\n")

            modules << m
        }

        Logger.info("Done!")
    }
}
