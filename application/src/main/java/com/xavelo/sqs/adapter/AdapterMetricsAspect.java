package com.xavelo.sqs.adapter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.xavelo.sqs.adapter.AdapterMetrics.Result;

import java.time.Instant;

@Aspect
@Component
public class AdapterMetricsAspect {

    @Around("@annotation(annotation)")
    public Object countAdapterInvocation(ProceedingJoinPoint joinPoint, CountAdapterInvocation annotation) throws Throwable {

        Instant start = Instant.now();
        try {
            Object proceed = joinPoint.proceed();
            count(annotation, Result.SUCCESS);
            return proceed;
        } catch (Exception e) {
            count(annotation, Result.ERROR);
            throw e;
        } finally {
            time(annotation, start, Instant.now());
        }

    }

    private void count(CountAdapterInvocation annotation, Result result) {
        AdapterMetrics.countAdapterInvocation(annotation.name(), annotation.type(), annotation.direction(), result);
    }

    private void time(CountAdapterInvocation annotation, Instant start, Instant end) {
        AdapterMetrics.timeAdapterDuration(annotation.name(), annotation.type(), annotation.direction(), start, end);
    }

}
