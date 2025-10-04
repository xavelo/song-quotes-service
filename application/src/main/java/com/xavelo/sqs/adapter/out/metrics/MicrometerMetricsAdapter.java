package com.xavelo.sqs.adapter.out.metrics;

import com.xavelo.common.metrics.Adapter;
import com.xavelo.common.metrics.CountAdapterInvocation;
import com.xavelo.sqs.port.out.MetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.xavelo.common.metrics.AdapterMetrics.Direction.OUT;
import static com.xavelo.common.metrics.AdapterMetrics.Type.METRICS;

/**
 * Adapter using Micrometer to record metrics.
 */
@Adapter
public class MicrometerMetricsAdapter implements MetricsPort {

    private static final String TOTAL_HITS_METRIC_NAME = "quote_hits_total";
    private static final String QUOTE_HITS_METRIC_NAME = "quote_hits_by_quote_total";
    private static final String TOTAL_STORED_METRIC_NAME = "quote_stored_total";

    private final MeterRegistry meterRegistry;
    private final Counter totalHitsCounter;
    private final Counter storedQuotesCounter;
    private final ConcurrentMap<Long, Counter> quoteHitsCounters = new ConcurrentHashMap<>();

    public MicrometerMetricsAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.totalHitsCounter = Counter.builder(TOTAL_HITS_METRIC_NAME)
                .description("Number of times a quote was requested")
                .register(meterRegistry);
        this.storedQuotesCounter = Counter.builder(TOTAL_STORED_METRIC_NAME)
                .description("Number of times a quote was stored")
                .register(meterRegistry);
    }

    @Override
    @CountAdapterInvocation(name = "increment-total-hits", direction = OUT, type = METRICS)
    public void incrementTotalHits() {
        totalHitsCounter.increment();
    }

    @Override
    @CountAdapterInvocation(name = "increment-stored-quotes", direction = OUT, type = METRICS)
    public void incrementStoredQuotes() {
        storedQuotesCounter.increment();
    }

    @Override
    @CountAdapterInvocation(name = "increment-quote-hits", direction = OUT, type = METRICS)
    public void incrementQuoteHits(Long quoteId) {
        if (quoteId == null) {
            return;
        }

        quoteHitsCounters
                .computeIfAbsent(quoteId, this::createQuoteHitsCounter)
                .increment();
    }

    private Counter createQuoteHitsCounter(Long quoteId) {
        return Counter.builder(QUOTE_HITS_METRIC_NAME)
                .description("Number of times a specific quote was requested")
                .tag("quote_id", quoteId.toString())
                .register(meterRegistry);
    }
}
