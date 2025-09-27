package com.xavelo.sqs.port.out;

/**
 * Port for retrieving the number of stored quotes.
 */
public interface QuotesCountPort {
    /**
     * @return total number of quotes
     */
    Long countQuotes();
}
