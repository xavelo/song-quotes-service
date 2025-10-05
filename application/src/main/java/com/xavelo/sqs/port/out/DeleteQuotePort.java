package com.xavelo.sqs.port.out;

import java.util.UUID;

public interface DeleteQuotePort {
    void deleteQuote(UUID id);
}
