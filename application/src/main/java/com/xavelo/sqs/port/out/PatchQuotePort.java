package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

public interface PatchQuotePort {
    void patchQuote(String id, Quote quote);
}
