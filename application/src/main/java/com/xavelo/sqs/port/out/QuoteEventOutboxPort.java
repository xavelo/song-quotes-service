package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.QuoteEvent;

import java.time.Duration;
import java.util.List;

public interface QuoteEventOutboxPort {
    void recordQuoteCreatedEvent(Quote quote);

    void recordQuoteHitEvent(Quote quote);

    List<QuoteEvent> fetchPendingEvents(int batchSize);

    void markEventPublished(Long id);

    void markEventFailed(Long id, String errorMessage, Duration retryDelay);
}
