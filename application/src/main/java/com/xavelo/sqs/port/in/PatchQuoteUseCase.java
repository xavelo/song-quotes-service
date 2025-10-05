package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

public interface PatchQuoteUseCase {
    void patchQuote(String id, Quote quote);
}
