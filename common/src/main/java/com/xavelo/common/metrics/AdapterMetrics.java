package com.xavelo.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for recording adapter invocation metrics using Micrometer.
 */
public final class AdapterMetrics {

    public static final String METRIC_ADAPTER_INVOCATIONS = "adapter.invocations";
    public static final String METRIC_ADAPTER_DURATION = "adapter.duration";
    private static final String TAG_ADAPTER_NAME = "adapter";
    private static final String TAG_ADAPTER_TYPE = "type";
    private static final String TAG_ADAPTER_DIRECTION = "direction";
    private static final String TAG_ADAPTER_RESULT = "result";

    private static final Logger log = LoggerFactory.getLogger(AdapterMetrics.class);

    private AdapterMetrics() {
    }

    /**
     * Records an adapter invocation counter using the provided metadata.
     */
    public static void countAdapterInvocation(String name, Type type, Direction direction, Result result) {
        Tags tags = Tags.of(
                TAG_ADAPTER_NAME, name,
                TAG_ADAPTER_TYPE, type.name(),
                TAG_ADAPTER_DIRECTION, direction.name(),
                TAG_ADAPTER_RESULT, result.name());
        Counter counter = Metrics.counter(METRIC_ADAPTER_INVOCATIONS, tags);
        counter.increment();
    }

    /**
     * Records the adapter duration in a timer.
     */
    public static void timeAdapterDuration(String name, Type type, Direction direction, Instant start, Instant end) {
        if (start == null || end == null) {
            log.warn("Unable to capture adapter duration for {} because start [{}] or end [{}] is null", name, start, end);
            return;
        }

        if (end.isBefore(start)) {
            log.warn("Unable to capture adapter duration for {} because end [{}] is before start [{}]", name, end, start);
            return;
        }

        Duration duration = Duration.between(start, end);
        log.debug(
                "Adapter {} of type {} with direction {} completed in {} ms",
                name,
                type,
                direction,
                duration.toMillis());

        Tags tags = Tags.of(
                TAG_ADAPTER_NAME, name,
                TAG_ADAPTER_TYPE, type.name(),
                TAG_ADAPTER_DIRECTION, direction.name());
        Timer timer = Metrics.timer(METRIC_ADAPTER_DURATION, tags);
        timer.record(duration);
    }

    public enum Type {
        HTTP,
        KAFKA,
        DATABASE,
        METRICS
    }

    public enum Direction {
        IN,
        OUT
    }

    public enum Result {
        SUCCESS,
        ERROR
    }
}
