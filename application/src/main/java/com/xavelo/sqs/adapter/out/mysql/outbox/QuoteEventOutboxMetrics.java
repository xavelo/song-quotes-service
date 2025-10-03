package com.xavelo.sqs.adapter.out.mysql.outbox;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import com.xavelo.adaptermetrics.Adapter;
import com.xavelo.adaptermetrics.CountAdapterInvocation;

import java.time.LocalDateTime;

import static com.xavelo.adaptermetrics.AdapterMetrics.Direction.OUT;
import static com.xavelo.adaptermetrics.AdapterMetrics.Type.METRICS;

/**
 * Publishes Micrometer gauges that expose the state of the quote event outbox.
 *
 * <p>The gauges are evaluated on scrape so they always reflect the latest
 * counts without having to keep an in-memory cache. They can be scraped by
 * Prometheus and visualized in Grafana to monitor the relay lag and
 * inflight deliveries.</p>
 */
@Adapter
public class QuoteEventOutboxMetrics {

    static final String OUTBOX_READY_METRIC = "quote_outbox_ready_events";
    static final String OUTBOX_DELAYED_METRIC = "quote_outbox_delayed_events";
    static final String OUTBOX_PROCESSING_METRIC = "quote_outbox_processing_events";

    private final QuoteEventOutboxRepository repository;

    public QuoteEventOutboxMetrics(MeterRegistry meterRegistry, QuoteEventOutboxRepository repository) {
        this.repository = repository;

        Gauge.builder(OUTBOX_READY_METRIC, this, QuoteEventOutboxMetrics::countReadyEvents)
                .description("Number of outbox events ready to be published")
                .register(meterRegistry);

        Gauge.builder(OUTBOX_DELAYED_METRIC, this, QuoteEventOutboxMetrics::countDelayedEvents)
                .description("Number of outbox events waiting for retry availability")
                .register(meterRegistry);

        Gauge.builder(OUTBOX_PROCESSING_METRIC, this, QuoteEventOutboxMetrics::countProcessingEvents)
                .description("Number of outbox events currently being processed")
                .register(meterRegistry);
    }

    @CountAdapterInvocation(name = "count-ready-outbox-events", direction = OUT, type = METRICS)
    double countReadyEvents() {
        LocalDateTime now = LocalDateTime.now();
        return repository.countByStatusAndAvailableAtLessThanEqual(QuoteEventStatus.PENDING, now);
    }

    @CountAdapterInvocation(name = "count-delayed-outbox-events", direction = OUT, type = METRICS)
    double countDelayedEvents() {
        LocalDateTime now = LocalDateTime.now();
        return repository.countByStatusAndAvailableAtAfter(QuoteEventStatus.PENDING, now);
    }

    @CountAdapterInvocation(name = "count-processing-outbox-events", direction = OUT, type = METRICS)
    double countProcessingEvents() {
        return repository.countByStatus(QuoteEventStatus.PROCESSING);
    }
}
