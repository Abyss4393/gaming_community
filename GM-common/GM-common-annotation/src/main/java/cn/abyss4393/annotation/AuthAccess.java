package cn.abyss4393.annotation;

import java.lang.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AuthAccess
 * @description TODO 放行注解
 * @date 4/9/2023
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {
    String desc() default "AuthAccess";
    Class<? extends Annotation> annotation() default Annotation.class;
}
