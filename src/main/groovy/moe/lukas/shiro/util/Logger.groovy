package moe.lukas.shiro.util

/**
 * Small logger
 * No one needs the Log4J overhead duh
 */
class Logger {
    /**
     * Private logger abstraction
     *
     * @todo logrotation
     * @param any loggable
     */
    private static void log(String tag, String loggable) {
        println("[${new Date().format('dd/MM/yy - HH:MM:SS')}] (${tag.toUpperCase()}) $loggable");
    }

    static def info = { log("info", it) }
    static def notice = { log("notice", it) }
    static def warn = { log("warning", it) }
    static def err = { log("error", it) }
    static def wtf = { log("wtf", it) }
}
