package com.xavelo.sqs.port.in;

public interface ExportQuotesUseCase {
    java.util.List<com.xavelo.sqs.application.domain.Quote> exportQuotes();
}
