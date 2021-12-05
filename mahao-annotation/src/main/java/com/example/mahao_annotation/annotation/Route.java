package com.example.mahao_annotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Route {

    /**
     * path of route
     */
    String path();


    String group() default "";

    int extras() default Integer.MIN_VALUE;

    /**
     * the priority of route
     *
     */
    int priority() default -1;

}
