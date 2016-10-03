package moe.lukas.shiro.util

import groovy.transform.CompileStatic

@CompileStatic
class Timer {

    static void setTimeout(long ms, Closure callback) {
        Thread t = new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Timeout${Thread.currentThread().id} - " + name)

            Thread.sleep(ms)
            callback.call()
        })

        t.start()
    }

    @SuppressWarnings("GroovyInfiniteLoopStatement")
    static Thread setInterval(long ms, Closure callback) {
        Thread t = new Thread({
            String name = Thread.currentThread().getName()
            Thread.currentThread().setName("Timer#Timeout${Thread.currentThread().id} - " + name)

            while (true) {
                callback.call()
                Thread.sleep(ms)
            }
        })

        t.start()

        return t
    }
}
