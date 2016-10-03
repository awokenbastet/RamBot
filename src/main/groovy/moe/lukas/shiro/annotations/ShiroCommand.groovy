package moe.lukas.shiro.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * A simple command-boilerplate
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ShiroCommand {
    String command()

    String usage() default ""

    boolean hidden() default false

    boolean adminOnly() default false

    boolean ownerOnly() default false
}
