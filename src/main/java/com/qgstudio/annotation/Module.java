package com.qgstudio.annotation;

import javax.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yinjunbiao
 * @version 1.0
 * 模块注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface Module {
    @NotNull
    String value();

    @NotNull
    String apiKey();
}
