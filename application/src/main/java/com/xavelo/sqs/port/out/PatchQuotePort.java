package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

import java.util.UUID;

public interface PatchQuotePort {
    void patchQuote(UUID id, Quote quote);
}
