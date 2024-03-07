package com.carrot.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLimiter {

    String key();

    String prefix() default "limit_";

    int period() default 1;

    int count();
}
