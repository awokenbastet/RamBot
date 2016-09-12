package moe.lukas.shiro.annotations

import groovy.transform.CompileStatic

import java.lang.annotation.Annotation

/**
 * Helper to parse ShiroMeta annotations
 */
@CompileStatic
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
    static HashMap parse(Class<?> c) {
        if (annotationPresent(c)) {
            HashMap meta = [:]

            c.getAnnotations().each { Annotation a ->
                if (a instanceof ShiroMeta) {
                    meta << [
                        enabled    : a.enabled(),
                        description: a.description(),
                        commands   : a.commands(),
                        hidden     : a.hidden()
                    ]
                }
            }

            return meta
        } else {
            return null
        }
    }
}
