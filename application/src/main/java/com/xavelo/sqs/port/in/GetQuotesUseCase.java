package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Quote;
import java.util.List;

public interface GetQuotesUseCase {
    List<Quote> getQuotes();
}
