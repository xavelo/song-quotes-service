package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;

public interface StoreQuotePort {
    Long storeQuote(Quote quote);
    List<Long> storeQuotes(List<Quote> quotes);
}
