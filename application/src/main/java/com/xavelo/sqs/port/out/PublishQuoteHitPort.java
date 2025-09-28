package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Quote;

public interface PublishQuoteHitPort {
    void publishQuoteHit(Quote quote);
}
