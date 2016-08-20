package moe.lukas.shiro.core

import moe.lukas.shiro.annotations.ShiroCommand
import moe.lukas.shiro.annotations.ShiroMetaParser
import moe.lukas.shiro.util.Logger

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
    static List<Class<? extends Object>> loadModules() {
        List<Class<? extends Object>> classes = []

        File shiroModules = new File("shiro_modules")
        if (!shiroModules.exists()) {
            shiroModules.mkdir()
        } else if (shiroModules.isFile()) {
            shiroModules.delete()
            shiroModules.mkdir()
        }

        new GroovyScriptEngine("shiro_modules").with {
            shiroModules.listFiles().each { File file ->
                classes << loadScriptByName(file.getName())
            }
        }

        return classes
    }

    /**
     * Load all modules into $instances
     */
    static void load() {
        Logger.info("Loading modules...")

        loadModules().each { Class<? extends Object> c ->
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
