package com.example.mahao_annotation.annotation;


import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Autowired {

    String name() default "";

    boolean required() default false;

}
