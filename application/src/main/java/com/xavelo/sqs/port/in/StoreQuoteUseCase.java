package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;

public interface StoreQuoteUseCase {
    String storeQuote(Quote quote);
    List<String> storeQuotes(List<Quote> quotes);
}
