package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

/**
 * Port for updating an existing quote.
 */
public interface UpdateQuotePort {
    void updateQuote(Quote quote);
}
