package moe.lukas.shiro.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ShiroCommand {
    public String command()
    public String usage() default null
}
