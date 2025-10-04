/**
 * Provides annotations and support for instrumenting adapter invocations.
 * <p>
 * Add {@link com.xavelo.common.metrics.EnableAdapterMetrics @EnableAdapterMetrics}
 * to a Spring configuration class and annotate adapter methods with
 * {@link com.xavelo.common.metrics.CountAdapterInvocation @CountAdapterInvocation}
 * to automatically emit counters and timers tagged with the adapter name, type,
 * direction, and invocation result.
 */
@NonNullApi
package com.xavelo.common.metrics;

import org.springframework.lang.NonNullApi;
