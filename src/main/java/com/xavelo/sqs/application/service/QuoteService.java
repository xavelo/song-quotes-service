package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.port.in.CountQuotesUseCase;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.GetQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuoteUseCase;
import com.xavelo.sqs.port.in.GetRandomQuoteUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import com.xavelo.sqs.port.in.GetTop10QuotesUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.port.in.ExportQuotesUseCase;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.QuotesCountPort;
import com.xavelo.sqs.port.out.StoreQuotePort;
import com.xavelo.sqs.port.out.IncrementPostsPort;
import com.xavelo.sqs.port.out.IncrementHitsPort;
import com.xavelo.sqs.port.out.MetricsPort;
import com.xavelo.sqs.port.out.LoadArtistQuoteCountsPort;
import com.xavelo.sqs.port.out.UpdateQuotePort;
import com.xavelo.sqs.port.out.LoadTop10QuotesPort;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import com.xavelo.sqs.port.out.ExportQuotesPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService implements StoreQuoteUseCase, GetQuotesUseCase, GetQuoteUseCase, DeleteQuoteUseCase, CountQuotesUseCase, GetRandomQuoteUseCase, GetArtistQuoteCountsUseCase, UpdateQuoteUseCase, GetTop10QuotesUseCase, ExportQuotesUseCase {

    private final StoreQuotePort storeQuotePort;
    private final LoadQuotePort loadQuotePort;
    private final QuotesCountPort quotesCountPort;
    private final DeleteQuotePort deleteQuotePort;
    private final IncrementPostsPort incrementPostsPort;
    private final IncrementHitsPort incrementHitsPort;
    private final MetricsPort metricsPort;
    private final LoadArtistQuoteCountsPort loadArtistQuoteCountsPort;
    private final UpdateQuotePort updateQuotePort;
    private final PublishQuoteCreatedPort publishQuoteCreatedPort;
    private final LoadTop10QuotesPort loadTop10QuotesPort;
    private final ExportQuotesPort exportQuotesPort;

    public QuoteService(StoreQuotePort storeQuotePort, LoadQuotePort loadQuotePort,
                        QuotesCountPort quotesCountPort, DeleteQuotePort deleteQuotePort,
                        IncrementPostsPort incrementPostsPort, IncrementHitsPort incrementHitsPort,
                        MetricsPort metricsPort,
                        LoadArtistQuoteCountsPort loadArtistQuoteCountsPort,
                        UpdateQuotePort updateQuotePort,
                        PublishQuoteCreatedPort publishQuoteCreatedPort,
                        LoadTop10QuotesPort loadTop10QuotesPort,
                        ExportQuotesPort exportQuotesPort) {
        this.storeQuotePort = storeQuotePort;
        this.loadQuotePort = loadQuotePort;
        this.quotesCountPort = quotesCountPort;
        this.deleteQuotePort = deleteQuotePort;
        this.incrementPostsPort = incrementPostsPort;
        this.incrementHitsPort = incrementHitsPort;
        this.metricsPort = metricsPort;
        this.loadArtistQuoteCountsPort = loadArtistQuoteCountsPort;
        this.updateQuotePort = updateQuotePort;
        this.publishQuoteCreatedPort = publishQuoteCreatedPort;
        this.loadTop10QuotesPort = loadTop10QuotesPort;
        this.exportQuotesPort = exportQuotesPort;
    }

    @Override
    public String exportQuotesAsSql() {
        return exportQuotesPort.exportQuotesAsSql();
    }

    @Override
    public List<Quote> getTop10Quotes() {
        return loadTop10QuotesPort.loadTop10Quotes();
    }

    @Override
    public Long storeQuote(Quote quote) {
        Quote toStore = QuoteHelper.sanitize(quote);
        Long id = storeQuotePort.storeQuote(toStore);
        Quote stored = QuoteHelper.withId(toStore, id);
        publishQuoteCreatedPort.publishQuoteCreated(stored);
        return id;
    }

    @Override
    public java.util.List<Long> storeQuotes(List<Quote> quotes) {
        java.util.List<Quote> sanitized = quotes.stream()
                .map(QuoteHelper::sanitize)
                .toList();
        java.util.List<Long> ids = storeQuotePort.storeQuotes(sanitized);
        for (int i = 0; i < ids.size(); i++) {
            Quote s = sanitized.get(i);
            Quote stored = QuoteHelper.withId(s, ids.get(i));
            publishQuoteCreatedPort.publishQuoteCreated(stored);
        }
        return ids;
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
            metricsPort.incrementHits();
            quote = QuoteHelper.incrementHits(quote);
        }
        return quote;
    }

    @Override
    public Quote getRandomQuote() {
        Quote quote = loadQuotePort.loadRandomQuote();
        if (quote != null) {
            // count how many times the quote has been served
            incrementPostsPort.incrementPosts(quote.id());
            quote = QuoteHelper.incrementPosts(quote);
        }
        return quote;
    }

    @Override
    public void updateQuote(Quote quote) {
        updateQuotePort.updateQuote(quote);
    }

    @Override
    public void deleteQuote(Long id) {
        deleteQuotePort.deleteQuote(id);
    }

    @Override
    public Long countQuotes() {
        return quotesCountPort.countQuotes();
    }

    @Override
    public java.util.List<ArtistQuoteCount> getArtistQuoteCounts() {
        return loadArtistQuoteCountsPort.loadArtistQuoteCounts()
                .stream()
                .sorted(java.util.Comparator.comparing(ArtistQuoteCount::quotes).reversed())
                .toList();
    }
}
