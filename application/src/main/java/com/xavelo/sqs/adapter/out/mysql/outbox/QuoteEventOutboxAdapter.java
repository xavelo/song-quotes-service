package com.xavelo.sqs.adapter.out.mysql.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.QuoteEvent;
import com.xavelo.sqs.application.domain.QuoteEventType;
import com.xavelo.sqs.port.out.QuoteEventOutboxPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class QuoteEventOutboxAdapter implements QuoteEventOutboxPort {

    private final QuoteEventOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public QuoteEventOutboxAdapter(QuoteEventOutboxRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void recordQuoteCreatedEvent(Quote quote) {
        persistEvent(quote, QuoteEventType.CREATED);
    }

    @Override
    @Transactional
    public void recordQuoteHitEvent(Quote quote) {
        persistEvent(quote, QuoteEventType.HIT);
    }

    private void persistEvent(Quote quote, QuoteEventType type) {
        try {
            String payload = objectMapper.writeValueAsString(quote);
            QuoteEventEntity entity = new QuoteEventEntity(type, QuoteEventStatus.PENDING, payload);
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize quote for outbox", e);
        }
    }

    @Override
    @Transactional
    public List<QuoteEvent> fetchPendingEvents(int batchSize) {
        List<QuoteEventEntity> entities = repository.findPendingEvents(
                QuoteEventStatus.PENDING,
                LocalDateTime.now(),
                PageRequest.of(0, batchSize)
        );

        entities.forEach(entity -> {
            entity.setStatus(QuoteEventStatus.PROCESSING);
            entity.setAttempts(entity.getAttempts() + 1);
        });

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private QuoteEvent toDomain(QuoteEventEntity entity) {
        try {
            Quote quote = objectMapper.readValue(entity.getPayload(), Quote.class);
            return new QuoteEvent(entity.getId(), entity.getType(), quote, entity.getAttempts());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize quote event payload", e);
        }
    }

    @Override
    @Transactional
    public void markEventPublished(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.setStatus(QuoteEventStatus.PUBLISHED);
            entity.setLastError(null);
        });
    }

    @Override
    @Transactional
    public void markEventFailed(Long id, String errorMessage, Duration retryDelay) {
        repository.findById(id).ifPresent(entity -> {
            entity.setStatus(QuoteEventStatus.PENDING);
            entity.setLastError(errorMessage);
            LocalDateTime nextAttempt = LocalDateTime.now().plus(retryDelay);
            entity.setAvailableAt(nextAttempt);
        });
    }
}
