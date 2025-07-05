package com.xavelo.sqs.adapter.out.metrics;

import com.xavelo.sqs.port.out.MetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Adapter using Micrometer to record metrics.
 */
@Component
public class MicrometerMetricsAdapter implements MetricsPort {

    private final Counter hitsCounter;

    public MicrometerMetricsAdapter(MeterRegistry meterRegistry) {
        this.hitsCounter = Counter.builder("quote_hits_total")
                .description("Number of times a quote was requested by id")
                .register(meterRegistry);
    }

    @Override
    public void incrementHits() {
        hitsCounter.increment();
    }
}
