package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;

public interface StoreQuoteUseCase {
    Long storeQuote(Quote quote);
    List<Long> storeQuotes(List<Quote> quotes);
}
