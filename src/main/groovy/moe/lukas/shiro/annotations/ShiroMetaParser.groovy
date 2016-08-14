package moe.lukas.shiro.annotations

import java.lang.annotation.Annotation

/**
 * Helper to parse ShiroMeta annotations
 */
class ShiroMetaParser {
    /**
     * Check if $c contains a ShiroMeta annotation
     * @param c
     * @return
     */
    static def annotationPresent(Class<?> c) {
        return c.isAnnotationPresent(ShiroMeta)
    }

    /**
     * Get all ShiroMeta values as map
     * @param c
     * @return
     */
    static def parse(Class<?> c) {
        if(annotationPresent(c)) {
            def meta = []

            c.getAnnotations().each { Annotation a ->
                if (a instanceof ShiroMeta) {
                    meta << [
                            enabled    : a.enabled(),
                            author     : a.author(),
                            description: a.description()
                    ]
                }
            }

            return meta
        } else {
            return null
        }
    }
}
