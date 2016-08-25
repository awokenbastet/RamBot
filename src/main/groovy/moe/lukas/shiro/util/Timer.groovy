package moe.lukas.shiro.util

class Timer {
    static void setTimeout(long ms, Closure callback) {
        new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Timeout - " + name)

            Thread.sleep(ms)
            callback.call()
        }).start()
    }

    static Thread setInterval(long ms, Closure callback) {
        return new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Interval - " + name)

            while (true) {
                callback.call()
                Thread.sleep(ms)
            }
        }).start()
    }
}
