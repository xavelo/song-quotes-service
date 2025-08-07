package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;
import java.util.List;

/**
 * Port to load the most popular quotes ordered by hits.
 */
public interface LoadTopQuotesPort {
    /**
     * Load the quotes sorted by hits descending. Implementations
     * should limit the result size to the desired number (e.g. 10).
     */
    List<Quote> loadTopQuotes();
}
