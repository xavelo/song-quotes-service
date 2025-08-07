package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;
import java.util.List;

/**
 * Use case for retrieving the most popular quotes by hits.
 */
public interface GetTopQuotesUseCase {
    List<Quote> getTopQuotes();
}
