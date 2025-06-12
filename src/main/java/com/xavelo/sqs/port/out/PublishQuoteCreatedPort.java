package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

public interface PublishQuoteCreatedPort {
    void publishQuoteCreated(Quote quote);
}
