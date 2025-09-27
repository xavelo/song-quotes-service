package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

import java.util.List;

public interface GetTop10QuotesUseCase {
    List<Quote> getTop10Quotes();
}
