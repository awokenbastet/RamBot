package moe.lukas.shiro

import groovy.transform.CompileStatic
import moe.lukas.shiro.core.Core
import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger
import moe.lukas.shiro.util.Timer

@CompileStatic
class Launcher {
    public static void main(String[] args) {
        Logger.info("Running from ${System.getProperty("user.dir")}")
        Logger.info("If this path is incorrect kill the program now and check your setup!")
        Logger.info(" --- waiting 5 seconds ---");
        System.out.println()

        Timer.setTimeout(5 * 1000, {
            String token = Brain.instance.get("token", "YOUR_TOKEN_HERE")

            if (token == null || token == "YOUR_TOKEN_HERE") {
                Logger.err("Please open 'brain.json' and enter your Discord-API-Token!")
                System.exit(1)
            } else {
                Core.boot(token)
            }
        })
    }
}
