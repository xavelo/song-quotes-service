package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;

public interface LoadTop10QuotesPort {
    List<Quote> loadTop10Quotes();
}
