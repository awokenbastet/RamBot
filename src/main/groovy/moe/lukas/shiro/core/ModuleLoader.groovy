package moe.lukas.shiro.core

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
    static Set<Class<? extends Object>> getModules() {
        Reflections reflections = new Reflections("moe.lukas.shiro.modules")
        return reflections.getSubTypesOf(Object)
    }

    static void load() {
        getModules().each { Class<? extends Object> c ->

        }
    }
}
