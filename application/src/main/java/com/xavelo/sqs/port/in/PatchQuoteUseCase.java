package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.UUID;

public interface PatchQuoteUseCase {
    void patchQuote(UUID id, Quote quote);
}
