package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.event.QuoteHitEvent;
import com.xavelo.sqs.application.service.event.QuoteStoredEvent;
import com.xavelo.sqs.port.out.MetricsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class QuoteMetricsServiceTest {

    private static final String QUOTE_ID = "44444444-4444-4444-4444-444444444444";

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private QuoteMetricsService quoteMetricsService;

    private Quote quote;

    @BeforeEach
    void setUp() {
        quote = new Quote(QUOTE_ID, "quote", "song", "album", 1990, "artist", 0, 1, null);
    }

    @Test
    void onQuoteHit_incrementsHitMetrics() {
        quoteMetricsService.onQuoteHit(new QuoteHitEvent(quote));

        verify(metricsPort).incrementTotalHits();
        verify(metricsPort).incrementQuoteHits(quote.id());
    }

    @Test
    void onQuoteStored_onlyIncrementsStoredMetric() {
        quoteMetricsService.onQuoteStored(new QuoteStoredEvent(quote));

        verify(metricsPort).incrementStoredQuotes();
        verifyNoMoreInteractions(metricsPort);
    }
}
