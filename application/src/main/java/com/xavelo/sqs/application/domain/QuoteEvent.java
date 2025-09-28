package com.xavelo.sqs.application.domain;

public record QuoteEvent(
        Long id,
        QuoteEventType type,
        Quote quote,
        int attempts
) {
}
