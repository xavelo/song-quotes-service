package com.xavelo.sqs.adapter.out.mysql.outbox;

public enum QuoteEventStatus {
    PENDING,
    PROCESSING,
    PUBLISHED
}
