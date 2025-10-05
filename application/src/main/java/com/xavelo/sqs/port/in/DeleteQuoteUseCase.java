package com.xavelo.sqs.port.in;

import java.util.UUID;

public interface DeleteQuoteUseCase {
    void deleteQuote(UUID id);
}