package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.port.in.CountQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuoteUseCase;
import com.xavelo.sqs.port.in.GetRandomQuoteUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import com.xavelo.sqs.port.in.GetTop10QuotesUseCase;
import com.xavelo.sqs.port.in.PatchQuoteUseCase;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.QuotesCountPort;
import com.xavelo.sqs.port.out.StoreQuotePort;
import com.xavelo.sqs.port.out.IncrementPostsPort;
import com.xavelo.sqs.port.out.IncrementHitsPort;
import com.xavelo.sqs.port.out.MetricsPort;
import com.xavelo.sqs.port.out.LoadArtistQuoteCountsPort;
import com.xavelo.sqs.port.out.LoadTop10QuotesPort;
import com.xavelo.sqs.port.out.PublishQuoteCreatedPort;
import com.xavelo.sqs.port.out.PatchQuotePort;
import com.xavelo.sqs.port.out.SyncArtistMetadataPort;
import com.xavelo.sqs.application.service.MetadataService;
import com.xavelo.sqs.application.domain.Artist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteService implements StoreQuoteUseCase, GetQuotesUseCase, GetQuoteUseCase, CountQuotesUseCase, GetRandomQuoteUseCase, GetArtistQuoteCountsUseCase, GetTop10QuotesUseCase, PatchQuoteUseCase {

    private static final Logger logger = LogManager.getLogger(QuoteService.class);

    private final StoreQuotePort storeQuotePort;
    private final LoadQuotePort loadQuotePort;
    private final QuotesCountPort quotesCountPort;
    private final IncrementPostsPort incrementPostsPort;
    private final IncrementHitsPort incrementHitsPort;
    private final MetricsPort metricsPort;
    private final LoadArtistQuoteCountsPort loadArtistQuoteCountsPort;
    private final PublishQuoteCreatedPort publishQuoteCreatedPort;
    private final LoadTop10QuotesPort loadTop10QuotesPort;
    private final PatchQuotePort patchQuotePort;
    private final SyncArtistMetadataPort syncArtistMetadataPort;
    private final MetadataService metadataService;

    public QuoteService(StoreQuotePort storeQuotePort, LoadQuotePort loadQuotePort,
                        QuotesCountPort quotesCountPort,
                        IncrementPostsPort incrementPostsPort, IncrementHitsPort incrementHitsPort,
                        MetricsPort metricsPort,
                        LoadArtistQuoteCountsPort loadArtistQuoteCountsPort,
                        PublishQuoteCreatedPort publishQuoteCreatedPort,
                        LoadTop10QuotesPort loadTop10QuotesPort,
                        PatchQuotePort patchQuotePort,
                        SyncArtistMetadataPort syncArtistMetadataPort,
                        MetadataService metadataService) {
        this.storeQuotePort = storeQuotePort;
        this.loadQuotePort = loadQuotePort;
        this.quotesCountPort = quotesCountPort;
        this.incrementPostsPort = incrementPostsPort;
        this.incrementHitsPort = incrementHitsPort;
        this.metricsPort = metricsPort;
        this.loadArtistQuoteCountsPort = loadArtistQuoteCountsPort;
        this.publishQuoteCreatedPort = publishQuoteCreatedPort;
        this.loadTop10QuotesPort = loadTop10QuotesPort;
        this.patchQuotePort = patchQuotePort;
        this.syncArtistMetadataPort = syncArtistMetadataPort;
        this.metadataService = metadataService;
    }

    @Override
    public void patchQuote(Long id, Quote quote) {
        patchQuotePort.patchQuote(id, quote);
    }

    @Override
    public List<Quote> getTop10Quotes() {
        return loadTop10QuotesPort.loadTop10Quotes();
    }

    @Override
    public Long storeQuote(Quote quote) {
        Quote toStore = QuoteHelper.sanitize(quote);
        Artist artistMetadata = metadataService.getArtistMetadata(toStore.artist());
        Long id = storeQuotePort.storeQuote(toStore, artistMetadata);
        Quote stored = QuoteHelper.withId(toStore, id);
        publishQuoteCreatedPort.publishQuoteCreated(stored);
        logger.debug("Artist {} (id {}, popularity {})", artistMetadata.name(), artistMetadata.id(), artistMetadata.popularity());
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
    public Long countQuotes() {
        return quotesCountPort.countQuotes();
    }

    @Override
    public java.util.List<ArtistQuoteCount> getArtistQuoteCounts() {
        return loadArtistQuoteCountsPort.loadArtistQuoteCounts()
                .stream()
                .map(artistQuoteCount -> {
                    if (artistQuoteCount.id() != null) {
                        return artistQuoteCount;
                    }

                    Artist artistMetadata = metadataService.getArtistMetadata(artistQuoteCount.artist());
                    if (artistMetadata == null || artistMetadata.id() == null) {
                        return artistQuoteCount;
                    }

                    syncArtistMetadataPort.syncArtistMetadata(artistQuoteCount.artist(), artistMetadata);
                    return new ArtistQuoteCount(artistMetadata.id(), artistQuoteCount.artist(), artistQuoteCount.quotes());
                })
                .sorted(java.util.Comparator.comparing(ArtistQuoteCount::quotes).reversed())
                .toList();
    }
}
