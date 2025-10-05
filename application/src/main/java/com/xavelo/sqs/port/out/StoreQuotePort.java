package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.Quote;

import java.util.List;
import java.util.UUID;

public interface StoreQuotePort {
    UUID storeQuote(Quote quote, Artist artistMetadata);
    List<UUID> storeQuotes(List<Quote> quotes, List<Artist> artistsMetadata);
}