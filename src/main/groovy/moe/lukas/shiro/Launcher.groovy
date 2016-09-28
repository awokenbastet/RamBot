package moe.lukas.shiro

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.util.Database
import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.util.Timer

@CompileStatic
class Launcher {
    static void main(String[] args) {
        Logger.info("Hi, I'm Shiro c:")
        Logger.info("Give me a moment to prepare myself...")

        boolean devEnv = System.getenv("ENVIRONMENT") == "DEV"

        File config = new File("config" + (devEnv ? ".dev.json" : ".json"))
        if (!config.exists()) {
            config.createNewFile()
            config.write("""
{
  "mysql": {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "pass": "root",
    "db": "${devEnv ? "shiro" : "shiro_dev"}"
  },
  "discord-token":"YOUR_TOKEN_HERE"
}
""")
            Logger.err("Please open ${config.name} and enter some data.")
            System.exit(1)
        } else {
            HashMap json = new JsonSlurper().parse(config) as HashMap

            println()

            if(devEnv) {
                Logger.warn("Running in development mode!")
            }

            Logger.info("Running from ${System.getProperty("user.dir")}")
            Logger.info("If this path is incorrect kill the program now and check your setup!")
            Logger.info(" --- waiting 5 seconds ---")
            System.out.println()

            Timer.setTimeout(5 * 1000, {
                Database.createInstance(json.mysql as HashMap)
                Core.boot(json["discord-token"] as String)

                Runtime.runtime.addShutdownHook(new Thread({
                    println("Meh. Someone told the OS to kill me :(")
                    println("Logging out...")
                    Core.logout()
                }))
            })
        }
    }
}
