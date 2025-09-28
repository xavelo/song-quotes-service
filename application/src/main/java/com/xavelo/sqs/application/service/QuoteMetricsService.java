package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.service.event.QuoteHitEvent;
import com.xavelo.sqs.application.service.event.QuoteStoredEvent;
import com.xavelo.sqs.port.out.MetricsPort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class QuoteMetricsService {

    private final MetricsPort metricsPort;

    public QuoteMetricsService(MetricsPort metricsPort) {
        this.metricsPort = metricsPort;
    }

    @EventListener
    public void onQuoteStored(QuoteStoredEvent event) {
        metricsPort.incrementStoredQuotes();
    }

    @EventListener
    public void onQuoteHit(QuoteHitEvent event) {
        metricsPort.incrementTotalHits();
        metricsPort.incrementQuoteHits(event.quote().id());
    }
}
