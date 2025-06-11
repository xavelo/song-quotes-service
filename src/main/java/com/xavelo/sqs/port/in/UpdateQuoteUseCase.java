package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

/**
 * Use case for updating an existing quote.
 */
public interface UpdateQuoteUseCase {
    /**
     * Update the provided quote. The quote id must not be null.
     *
     * @param quote quote with new values
     */
    void updateQuote(Quote quote);
}
