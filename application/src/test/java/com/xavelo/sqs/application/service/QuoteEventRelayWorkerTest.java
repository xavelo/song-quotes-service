package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.QuoteEvent;
import com.xavelo.sqs.application.domain.QuoteEventType;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import com.xavelo.sqs.port.out.PublishQuoteHitPort;
import com.xavelo.sqs.port.out.QuoteEventOutboxPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteEventRelayWorkerTest {

    @Mock
    private QuoteEventOutboxPort quoteEventOutboxPort;

    @Mock
    private PublishQuoteCreatedPort publishQuoteCreatedPort;

    @Mock
    private PublishQuoteHitPort publishQuoteHitPort;

    private QuoteEventRelayWorker worker;

    @BeforeEach
    void setUp() {
        worker = new QuoteEventRelayWorker(quoteEventOutboxPort, publishQuoteCreatedPort, publishQuoteHitPort, 10, Duration.ofSeconds(30));
    }

    @Test
    void relayOutbox_publishesCreatedEvent() {
        Quote quote = new Quote("quote-1", "quote", "song", "album", 1990, "artist", 0, 0, null);
        QuoteEvent event = new QuoteEvent(1L, QuoteEventType.CREATED, quote, 1);
        when(quoteEventOutboxPort.fetchPendingEvents(10)).thenReturn(List.of(event));

        worker.relayOutbox();

        verify(publishQuoteCreatedPort).publishQuoteCreated(quote);
        verify(quoteEventOutboxPort).markEventPublished(1L);
    }

    @Test
    void relayOutbox_handlesPublishFailure() {
        Quote quote = new Quote("quote-1", "quote", "song", "album", 1990, "artist", 0, 0, null);
        QuoteEvent event = new QuoteEvent(1L, QuoteEventType.CREATED, quote, 1);
        when(quoteEventOutboxPort.fetchPendingEvents(10)).thenReturn(List.of(event));
        doThrow(new RuntimeException("boom")).when(publishQuoteCreatedPort).publishQuoteCreated(quote);

        worker.relayOutbox();

        verify(quoteEventOutboxPort).markEventFailed(eq(1L), eq("boom"), eq(Duration.ofSeconds(30)));
    }

    @Test
    void relayOutbox_publishesHitEvent() {
        Quote quote = new Quote("quote-1", "quote", "song", "album", 1990, "artist", 0, 1, null);
        QuoteEvent event = new QuoteEvent(2L, QuoteEventType.HIT, quote, 1);
        when(quoteEventOutboxPort.fetchPendingEvents(10)).thenReturn(List.of(event));

        worker.relayOutbox();

        verify(publishQuoteHitPort).publishQuoteHit(quote);
        verify(quoteEventOutboxPort).markEventPublished(2L);
    }

    @Test
    void relayOutbox_noEventsDoesNothing() {
        when(quoteEventOutboxPort.fetchPendingEvents(10)).thenReturn(List.of());

        worker.relayOutbox();

        verify(quoteEventOutboxPort, times(1)).fetchPendingEvents(10);
        verify(publishQuoteCreatedPort, times(0)).publishQuoteCreated(org.mockito.ArgumentMatchers.any());
        verify(publishQuoteHitPort, times(0)).publishQuoteHit(org.mockito.ArgumentMatchers.any());
    }
}
