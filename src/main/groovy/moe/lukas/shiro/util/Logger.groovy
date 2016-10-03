package moe.lukas.shiro.util

import groovy.transform.CompileStatic

/**
 * Small logger
 * No one needs the Log4J overhead duh
 */
@CompileStatic
class Logger {
    /**
     * Abstraction of println() with formatting
     *
     * @param tag The tag to use
     * @param loggable A string to log
     */
    static void log(String tag, String loggable) {
        println("[${new Date().format('dd/MM/yy - hh:mm:ss a')}] (${tag.toUpperCase()}) $loggable")
    }

    static void info(String m) { log("info", m) }

    static void notice(String m) { log("notice", m) }

    static void warn(String m) { log("warn", m) }

    static void err(String m) { log("err", m) }

    static void wtf(String m) { log("wtf", m) }
}
