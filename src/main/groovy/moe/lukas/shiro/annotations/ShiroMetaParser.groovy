package moe.lukas.shiro.annotations

import java.lang.annotation.Annotation

class ShiroMetaParser {
    static def parse(Class<?> c) {
        def meta = [];

        c.getAnnotations().each { Annotation a ->
            if (a instanceof ShiroMeta) {
                meta << [
                        enabled    : a.enabled(),
                        author     : a.author(),
                        description: a.description()
                ];
            }
        }

        return meta;
    }
}
