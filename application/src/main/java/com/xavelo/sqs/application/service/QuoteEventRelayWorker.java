package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.QuoteEvent;
import com.xavelo.sqs.application.domain.QuoteEventType;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import com.xavelo.sqs.port.out.PublishQuoteHitPort;
import com.xavelo.sqs.port.out.QuoteEventOutboxPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class QuoteEventRelayWorker {

    private static final Logger logger = LogManager.getLogger(QuoteEventRelayWorker.class);

    private final QuoteEventOutboxPort quoteEventOutboxPort;
    private final PublishQuoteCreatedPort publishQuoteCreatedPort;
    private final PublishQuoteHitPort publishQuoteHitPort;
    private final AtomicInteger batchSize;
    private final Duration retryDelay;

    public QuoteEventRelayWorker(
            QuoteEventOutboxPort quoteEventOutboxPort,
            PublishQuoteCreatedPort publishQuoteCreatedPort,
            PublishQuoteHitPort publishQuoteHitPort,
            @Value("${quote-events.outbox.worker.batch-size:25}") int batchSize,
            @Value("${quote-events.outbox.worker.retry-delay:PT30S}") Duration retryDelay) {
        this.quoteEventOutboxPort = quoteEventOutboxPort;
        this.publishQuoteCreatedPort = publishQuoteCreatedPort;
        this.publishQuoteHitPort = publishQuoteHitPort;
        this.batchSize = new AtomicInteger(batchSize);
        this.retryDelay = retryDelay;
    }

    @Scheduled(fixedDelayString = "${quote-events.outbox.worker.delay:5000}")
    public void relayOutbox() {
        int currentBatchSize = batchSize.get();
        List<QuoteEvent> events = quoteEventOutboxPort.fetchPendingEvents(currentBatchSize);
        for (QuoteEvent event : events) {
            try {
                publishEvent(event);
                quoteEventOutboxPort.markEventPublished(event.id());
            } catch (Exception ex) {
                logger.error("Failed to publish quote event {}: {}", event.id(), ex.getMessage(), ex);
                quoteEventOutboxPort.markEventFailed(event.id(), ex.getMessage(), retryDelay);
            }
        }
    }

    public void updateBatchSize(int newBatchSize) {
        batchSize.set(newBatchSize);
    }

    private void publishEvent(QuoteEvent event) {
        if (event.type() == QuoteEventType.CREATED) {
            publishQuoteCreatedPort.publishQuoteCreated(event.quote());
        } else if (event.type() == QuoteEventType.HIT) {
            publishQuoteHitPort.publishQuoteHit(event.quote());
        } else {
            logger.warn("Unsupported quote event type {} for event {}", event.type(), event.id());
        }
    }
}
