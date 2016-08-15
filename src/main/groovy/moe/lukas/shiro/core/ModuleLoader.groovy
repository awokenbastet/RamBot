package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMetaParser
import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger
import org.reflections.Reflections

/**
 * Class to help loading/finding modules
 * Reflection > all ;)
 */
class ModuleLoader {
    private static LinkedHashMap modules = []

    /**
     * Returns all present modules
     * @return
     */
    static Set<Class<? extends IModule>> getModules() {
        Reflections reflections = new Reflections("shiro_modules")
        return reflections.getSubTypesOf(IModule)
    }

    /**
     * Load all modules into $instances
     */
    static void load() {
        Logger.info("Loading modules...")
        getModules().each { Class<? extends IModule> c ->
            def m = [
                name      : c.name,
                properties: ShiroMetaParser.parse(c),
                instance  : c.newInstance()
            ]

            print("${m.name} reacts to [|")
            m.properties.commands.each { ShiroCommand it ->
                print(Brain.instance.get() + it.command() + "|")
            }
            print("]")

            modules << m
        }
    }

    /**
     * Loop through $instances
     * @param callback
     */
    static void each(Closure callback) {
        modules.each(callback)
    }
}
