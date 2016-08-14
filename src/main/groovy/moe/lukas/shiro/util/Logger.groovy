package moe.lukas.shiro.util

/**
 * Small logger
 * No one needs the Log4J overhead duh
 */
class Logger {
    /**
     * Abstraction of println() with formatting
     *
     * @param tag The tag to use
     * @param loggable A string to log
     */
    private static void log(String tag, String loggable) {
        println("[${new Date().format('dd/MM/yy - hh:mm:ss a')}] (${tag.toUpperCase()}) $loggable")
    }

    static Closure info = { log("info", it) }
    static Closure notice = { log("notice", it) }
    static Closure warn = { log("warning", it) }
    static Closure err = { log("error", it) }
    static Closure wtf = { log("wtf", it) }
}
