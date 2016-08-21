package moe.lukas.shiro.annotations

import java.lang.annotation.Target
import java.lang.annotation.Retention
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * Meta-Annotation to store some plugin infos
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ShiroMeta {
    boolean enabled() default false

    String description() default ""

    String author() default "anonymous"

    ShiroCommand[] commands()
}
