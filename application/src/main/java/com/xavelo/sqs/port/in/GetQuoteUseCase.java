package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;

public interface GetQuoteUseCase {
    Quote getQuote(String id);
}
