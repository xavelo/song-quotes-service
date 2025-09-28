package com.xavelo.sqs.application.service.event;

import com.xavelo.sqs.application.domain.Quote;

public record QuoteStoredEvent(Quote quote) {
}
