package moe.lukas.shiro

import moe.lukas.shiro.core.Core
import moe.lukas.shiro.util.Logger

class Launcher {
    public static void main(String[] args) {
        Logger.info("Running from ${System.getProperty("user.dir")}")
        Logger.info("If this path is incorrect kill the program now and check your setup!")

        Core.boot()
    }
}
