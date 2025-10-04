package com.xavelo.common.metrics;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Enables adapter metrics instrumentation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AdapterMetricsAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public @interface EnableAdapterMetrics {
}
