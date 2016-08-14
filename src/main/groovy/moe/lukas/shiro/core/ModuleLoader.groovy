package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroMetaParser
import org.reflections.Reflections

/**
 * Class to help loading/finding modules
 * Reflection > all ;)
 */
class ModuleLoader {
    /**
     * Returns all present modules
     * @return
     */
    static Set<Class<? extends IModule>> getModules() {
        Reflections reflections = new Reflections("shiro_modules")
        return reflections.getSubTypesOf(IModule)
    }

    static void load() {
        getModules().each { Class<? extends IModule> c ->
            def meta = ShiroMetaParser.parse(c)
            println "Name: ${c.name}"
            println "Properties: ${meta.join("|")}"
        }
    }
}
