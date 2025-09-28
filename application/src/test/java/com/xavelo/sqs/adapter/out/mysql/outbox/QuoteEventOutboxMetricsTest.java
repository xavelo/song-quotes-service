package com.xavelo.sqs.adapter.out.mysql.outbox;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteEventOutboxMetricsTest {

    @Mock
    private QuoteEventOutboxRepository repository;

    private SimpleMeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        new QuoteEventOutboxMetrics(meterRegistry, repository);
    }

    @Test
    void gaugesReflectOutboxCounts() {
        when(repository.countByStatusAndAvailableAtLessThanEqual(eq(QuoteEventStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(5L);
        when(repository.countByStatusAndAvailableAtAfter(eq(QuoteEventStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(2L);
        when(repository.countByStatus(QuoteEventStatus.PROCESSING)).thenReturn(3L);

        double ready = meterRegistry.get(QuoteEventOutboxMetrics.OUTBOX_READY_METRIC).gauge().value();
        double delayed = meterRegistry.get(QuoteEventOutboxMetrics.OUTBOX_DELAYED_METRIC).gauge().value();
        double processing = meterRegistry.get(QuoteEventOutboxMetrics.OUTBOX_PROCESSING_METRIC).gauge().value();

        assertThat(ready).isEqualTo(5.0d);
        assertThat(delayed).isEqualTo(2.0d);
        assertThat(processing).isEqualTo(3.0d);

        verify(repository).countByStatusAndAvailableAtLessThanEqual(eq(QuoteEventStatus.PENDING), any(LocalDateTime.class));
        verify(repository).countByStatusAndAvailableAtAfter(eq(QuoteEventStatus.PENDING), any(LocalDateTime.class));
        verify(repository).countByStatus(QuoteEventStatus.PROCESSING);
    }
}
