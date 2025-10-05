package com.xavelo.sqs.port.out;

import java.util.UUID;

/**
 * Port for publishing application metrics.
 */
public interface MetricsPort {
    /**
     * Increment the global hits counter when any quote is served.
     */
    void incrementTotalHits();

    /**
     * Increment the global counter when a quote is stored.
     */
    void incrementStoredQuotes();

    /**
     * Increment the hits counter for a specific quote.
     *
     * @param quoteId the identifier of the quote that was served
     */
    void incrementQuoteHits(UUID quoteId);
}
