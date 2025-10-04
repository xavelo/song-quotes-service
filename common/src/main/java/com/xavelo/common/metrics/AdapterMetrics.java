package com.xavelo.common.metrics;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

import static io.micrometer.core.instrument.Tags.of;
import static java.util.Locale.UK;

public final class AdapterMetrics {

    private static final Logger logger = LogManager.getLogger(AdapterMetrics.class);

    public static void countAdapterInvocation(String adapterName, Type type, Direction direction, Result result) {
        Metrics.counter(
                "adapter.invocation",
                of(
                        Tag.of("name", adapterName),
                        Tag.of("type", type.name().toLowerCase(UK)),
                        Tag.of("direction", direction.name().toLowerCase(UK)),
                        Tag.of("result", result.name().toLowerCase(UK))
                )
            )
            .increment();
    }

    public static void timeAdapterDuration(String metricName, Type type, Direction direction, Instant start, Instant end) {
        try {
            timeAdapterDuration(metricName, type, direction, Duration.between(start, end));
        } catch (DateTimeException | ArithmeticException e) {
            logger.error("Failed to compute adapter duration for {} - {} - {}", metricName, type, direction, e);
        }
    }

    public static void timeAdapterDuration(String adapterName, Type type, Direction direction, Duration duration) {
        logger.info(
            "adapter duration: {} - {} - {} -> {} ms",
            adapterName,
            type.name(),
            direction.name(),
            duration.toMillis()
        );
        Timer.builder("adapter.duration")
            .tags(of(
                    Tag.of("name", adapterName),
                    Tag.of("type", type.name().toLowerCase(UK)),
                    Tag.of("direction", direction.name().toLowerCase(UK))
            ))
            .publishPercentiles(0.95, 0.99)
            .register(Metrics.globalRegistry)
            .record(duration);
    }

    public enum Type {
        HTTP,
        KAFKA,
        MYSQL,
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
