package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;
import java.util.List;

public interface LoadQuotePort {
    List<Quote> loadQuotes();

    Quote loadQuote(String id);

    /**
     * Retrieve a random quote from the storage.
     *
     * @return a random {@link Quote} or {@code null} if none available
     */
    Quote loadRandomQuote();
}
