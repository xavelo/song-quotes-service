package com.xavelo.adaptermetrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.Instant;

@Aspect
public class AdapterMetricsAspect {

    @Around("@annotation(annotation)")
    public Object countAdapterInvocation(ProceedingJoinPoint joinPoint, CountAdapterInvocation annotation) throws Throwable {
        Instant start = Instant.now();
        try {
            Object proceed = joinPoint.proceed();
            count(annotation, AdapterMetrics.Result.SUCCESS);
            return proceed;
        } catch (Throwable t) {
            count(annotation, AdapterMetrics.Result.ERROR);
            throw t;
        } finally {
            time(annotation, start, Instant.now());
        }
    }

    private void count(CountAdapterInvocation annotation, AdapterMetrics.Result result) {
        AdapterMetrics.countAdapterInvocation(annotation.name(), annotation.type(), annotation.direction(), result);
    }

    private void time(CountAdapterInvocation annotation, Instant start, Instant end) {
        AdapterMetrics.timeAdapterDuration(annotation.name(), annotation.type(), annotation.direction(), start, end);
    }
}
