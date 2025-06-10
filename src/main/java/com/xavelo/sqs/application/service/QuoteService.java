package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.CountQuotesUseCase;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.GetQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuoteUseCase;
import com.xavelo.sqs.port.in.GetRandomQuoteUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.QuotesCountPort;
import com.xavelo.sqs.port.out.StoreQuotePort;
import com.xavelo.sqs.port.out.IncrementPostsPort;
import com.xavelo.sqs.port.out.IncrementHitsPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService implements StoreQuoteUseCase, GetQuotesUseCase, GetQuoteUseCase, DeleteQuoteUseCase, CountQuotesUseCase, GetRandomQuoteUseCase {

    private final StoreQuotePort storeQuotePort;
    private final LoadQuotePort loadQuotePort;
    private final QuotesCountPort quotesCountPort;
    private final DeleteQuotePort deleteQuotePort;
    private final IncrementPostsPort incrementPostsPort;
    private final IncrementHitsPort incrementHitsPort;

    public QuoteService(StoreQuotePort storeQuotePort, LoadQuotePort loadQuotePort,
                        QuotesCountPort quotesCountPort, DeleteQuotePort deleteQuotePort,
                        IncrementPostsPort incrementPostsPort, IncrementHitsPort incrementHitsPort) {
        this.storeQuotePort = storeQuotePort;
        this.loadQuotePort = loadQuotePort;
        this.quotesCountPort = quotesCountPort;
        this.deleteQuotePort = deleteQuotePort;
        this.incrementPostsPort = incrementPostsPort;
        this.incrementHitsPort = incrementHitsPort;
    }

    @Override
    public Long storeQuote(Quote quote) {
        Quote toStore = new Quote(
                quote.id(),
                quote.quote(),
                quote.song(),
                quote.album(),
                quote.year(),
                quote.artist(),
                0,
                0
        );
        return storeQuotePort.storeQuote(toStore);
    }

    @Override
    public java.util.List<Long> storeQuotes(List<Quote> quotes) {
        java.util.List<Quote> sanitized = quotes.stream()
                .map(q -> new Quote(
                        q.id(),
                        q.quote(),
                        q.song(),
                        q.album(),
                        q.year(),
                        q.artist(),
                        0,
                        0
                ))
                .toList();
        return storeQuotePort.storeQuotes(sanitized);
    }

    @Override
    public java.util.List<Quote> getQuotes() {
        return loadQuotePort.loadQuotes();
    }

    @Override
    public Quote getQuote(Long id) {
        Quote quote = loadQuotePort.loadQuote(id);
        if (quote != null) {
            incrementHitsPort.incrementHits(id);
            int hitsCurrent = quote.hits() != null ? quote.hits() : 0;
            quote = new Quote(
                    quote.id(),
                    quote.quote(),
                    quote.song(),
                    quote.album(),
                    quote.year(),
                    quote.artist(),
                    quote.posts(),
                    hitsCurrent + 1
            );
        }
        return quote;
    }

    @Override
    public Quote getRandomQuote() {
        Quote quote = loadQuotePort.loadRandomQuote();
        if (quote != null) {
            // count how many times the quote has been served
            incrementPostsPort.incrementPosts(quote.id());
            int current = quote.posts() != null ? quote.posts() : 0;
            quote = new Quote(
                    quote.id(),
                    quote.quote(),
                    quote.song(),
                    quote.album(),
                    quote.year(),
                    quote.artist(),
                    current + 1,
                    quote.hits()
            );
        }
        return quote;
    }

    @Override
    public void deleteQuote(Long id) {
        deleteQuotePort.deleteQuote(id);
    }

    @Override
    public Long countQuotes() {
        return quotesCountPort.countQuotes();
    }
}
