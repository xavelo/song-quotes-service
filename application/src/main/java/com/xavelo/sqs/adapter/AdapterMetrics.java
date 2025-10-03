package com.xavelo.sqs.adapter;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

import static io.micrometer.core.instrument.Tags.of;
import static java.util.Locale.UK;

public class AdapterMetrics {

    private static final Logger logger = LogManager.getLogger(AdapterMetrics.class);

    public static void countAdapterInvocation(String adapterName, Type type, Direction direction, Result result) {
        logger.debug("countAdapterInvocation: {} - {} - {} - {}", adapterName, type.name(), direction.name(), result.name());
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

    }

    public enum Type {
        HTTP,
        KAFKA
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
