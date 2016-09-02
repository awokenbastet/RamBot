package moe.lukas.shiro.util

class Timer {
    static void setTimeout(long ms, Closure callback) {
        Thread t = new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Timeout - " + name)

            Thread.sleep(ms)
            callback.call()
        })

        t.setName("TIMEOUT#${t.id}")
        t.start()
    }

    static Thread setInterval(long ms, Closure callback) {
        Thread t = new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Interval - " + name)

            while (true) {
                callback.call()
                Thread.sleep(ms)
            }
        })

        t.setName("INTERVAL#${t.id}")
        t.start()

        return t
    }
}
