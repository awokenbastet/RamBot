package moe.lukas.shiro

import moe.lukas.shiro.core.Core
import moe.lukas.shiro.util.Brain
import moe.lukas.shiro.util.Logger

class Launcher {
    public static void main(String[] args) {
        Logger.info("Running from ${System.getProperty("user.dir")}")
        Logger.info("If this path is incorrect kill the program now and check your setup!")

        String token = Brain.instance.get("api.token")
        if (token == null) {
            Brain.instance.set("api.token", "YOUR_TOKEN_HERE")
            Logger.err("Please open 'brain.json' and enter your Discord-API-Token!")
            System.exit(1)
        } else {
            Core.boot(token)
        }
    }
}
