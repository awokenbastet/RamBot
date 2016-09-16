package moe.lukas.shiro.util

import groovy.transform.CompileStatic

@CompileStatic
@Singleton(strict = false)
class Config extends AbstractStorage {
    private Config() {
        super("config.json")
    }
}
