package moe.lukas.shiro.util

import groovy.transform.CompileStatic

@CompileStatic
@Singleton(strict = false)
class Brain extends AbstractStorage {
    private Brain() {
        super("brain.json")
    }
}
