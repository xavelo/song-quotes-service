package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.UUID;

public interface GetQuoteUseCase {
    Quote getQuote(UUID id);
}
