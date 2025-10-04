package com.xavelo.common.metrics;

import java.time.Instant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect that captures adapter metrics for annotated methods.
 */
@Aspect
public class AdapterMetricsAspect {

    @Around("@annotation(annotation)")
    public Object recordMetrics(ProceedingJoinPoint joinPoint, CountAdapterInvocation annotation) throws Throwable {
        Instant start = Instant.now();
        try {
            Object result = joinPoint.proceed();
            AdapterMetrics.countAdapterInvocation(
                    annotation.name(), annotation.type(), annotation.direction(), AdapterMetrics.Result.SUCCESS);
            return result;
        } catch (Throwable throwable) {
            AdapterMetrics.countAdapterInvocation(
                    annotation.name(), annotation.type(), annotation.direction(), AdapterMetrics.Result.ERROR);
            throw throwable;
        } finally {
            Instant end = Instant.now();
            AdapterMetrics.timeAdapterDuration(annotation.name(), annotation.type(), annotation.direction(), start, end);
        }
    }
}
