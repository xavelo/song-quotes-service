package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;
import java.util.UUID;

public interface StoreQuoteUseCase {
    UUID storeQuote(Quote quote);
    List<UUID> storeQuotes(List<Quote> quotes);
}
