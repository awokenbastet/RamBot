package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroMetaParser
import org.reflections.Reflections

/**
 * Class to help loading/finding modules
 * Reflection > all ;)
 */
class ModuleLoader {
    private static LinkedHashMap instances = []

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
        getModules().each { Class<? extends IModule> c ->
            instances << [
                    name      : c.name,
                    properties: ShiroMetaParser.parse(c),
                    instance  : c.newInstance()
            ]
        }
    }

    /**
     * Loop through $instances
     * @param callback
     */
    static void each(Closure callback) {
        instances.each(callback)
    }
}
