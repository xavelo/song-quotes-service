package com.xavelo.sqs.port.in;

/**
 * Use case for retrieving the number of stored quotes.
 */
public interface CountQuotesUseCase {
    /**
     * @return total number of quotes
     */
    Long countQuotes();
}
