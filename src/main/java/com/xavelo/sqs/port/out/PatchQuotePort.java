package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

public interface PatchQuotePort {
    void patchQuote(Long id, Quote quote);
}
