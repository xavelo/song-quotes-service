package com.xavelo.common.metrics;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate adapter methods to automatically record invocation metrics.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CountAdapterInvocation {
    String name();
    AdapterMetrics.Direction direction();
    AdapterMetrics.Type type();
}
