package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.application.service.event.QuoteHitEvent;
import com.xavelo.sqs.application.service.event.QuoteStoredEvent;
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
import com.xavelo.sqs.port.out.LoadArtistQuoteCountsPort;
import com.xavelo.sqs.port.out.LoadTop10QuotesPort;
import com.xavelo.sqs.port.out.QuoteEventOutboxPort;
import com.xavelo.sqs.port.out.PatchQuotePort;
import com.xavelo.sqs.port.out.SyncArtistMetadataPort;
import com.xavelo.sqs.application.service.MetadataService;
import com.xavelo.sqs.application.domain.Artist;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
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
    private final LoadArtistQuoteCountsPort loadArtistQuoteCountsPort;
    private final QuoteEventOutboxPort quoteEventOutboxPort;
    private final LoadTop10QuotesPort loadTop10QuotesPort;
    private final PatchQuotePort patchQuotePort;
    private final SyncArtistMetadataPort syncArtistMetadataPort;
    private final MetadataService metadataService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public QuoteService(StoreQuotePort storeQuotePort, LoadQuotePort loadQuotePort,
                        QuotesCountPort quotesCountPort,
                        IncrementPostsPort incrementPostsPort, IncrementHitsPort incrementHitsPort,
                        LoadArtistQuoteCountsPort loadArtistQuoteCountsPort,
                        QuoteEventOutboxPort quoteEventOutboxPort,
                        LoadTop10QuotesPort loadTop10QuotesPort,
                        PatchQuotePort patchQuotePort,
                        SyncArtistMetadataPort syncArtistMetadataPort,
                        MetadataService metadataService,
                        ApplicationEventPublisher applicationEventPublisher) {
        this.storeQuotePort = storeQuotePort;
        this.loadQuotePort = loadQuotePort;
        this.quotesCountPort = quotesCountPort;
        this.incrementPostsPort = incrementPostsPort;
        this.incrementHitsPort = incrementHitsPort;
        this.loadArtistQuoteCountsPort = loadArtistQuoteCountsPort;
        this.quoteEventOutboxPort = quoteEventOutboxPort;
        this.loadTop10QuotesPort = loadTop10QuotesPort;
        this.patchQuotePort = patchQuotePort;
        this.syncArtistMetadataPort = syncArtistMetadataPort;
        this.metadataService = metadataService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void patchQuote(String id, Quote quote) {
        patchQuotePort.patchQuote(id, quote);
    }

    @Override
    public List<Quote> getTop10Quotes() {
        return loadTop10QuotesPort.loadTop10Quotes();
    }

    @Override
    @Transactional
    public String storeQuote(Quote quote) {
        Quote toStore = QuoteHelper.sanitize(quote);
        Artist artistMetadata = metadataService.getArtistMetadata(toStore.artist());
        String id = storeQuotePort.storeQuote(toStore, artistMetadata);
        Quote stored = QuoteHelper.withSpotifyArtistId(toStore, id, artistMetadata != null ? artistMetadata.id() : null);
        quoteEventOutboxPort.recordQuoteCreatedEvent(stored);
        applicationEventPublisher.publishEvent(new QuoteStoredEvent(stored));
        if (artistMetadata != null) {
            logger.debug("Artist {} (id {}, popularity {})", artistMetadata.name(), artistMetadata.id(), artistMetadata.popularity());
        }
        return id;
    }

    @Override
    @Transactional
    public List<String> storeQuotes(List<Quote> quotes) {
        List<Quote> sanitized = quotes.stream()
                .map(QuoteHelper::sanitize)
                .toList();
        List<String> ids = storeQuotePort.storeQuotes(sanitized);
        for (int i = 0; i < ids.size(); i++) {
            Quote s = sanitized.get(i);
            Quote stored = QuoteHelper.withId(s, ids.get(i));
            quoteEventOutboxPort.recordQuoteCreatedEvent(stored);
            applicationEventPublisher.publishEvent(new QuoteStoredEvent(stored));
        }
        return ids;
    }

    @Override
    public List<Quote> getQuotes() {
        return loadQuotePort.loadQuotes();
    }

    @Override
    @Transactional
    public Quote getQuote(String id) {
        Quote quote = loadQuotePort.loadQuote(id);
        if (quote != null) {
            incrementHitsPort.incrementHits(id);
            quote = QuoteHelper.incrementHits(quote);
            quoteEventOutboxPort.recordQuoteHitEvent(quote);
            applicationEventPublisher.publishEvent(new QuoteHitEvent(quote));
        }
        return quote;
    }

    @Override
    @Transactional
    public Quote getRandomQuote() {
        Quote quote = loadQuotePort.loadRandomQuote();
        if (quote != null) {
            incrementHitsPort.incrementHits(quote.id());
            quote = QuoteHelper.incrementHits(quote);
            quoteEventOutboxPort.recordQuoteHitEvent(quote);
            applicationEventPublisher.publishEvent(new QuoteHitEvent(quote));
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

                    logger.debug("getting metadata for artist {}", artistQuoteCount.artist());
                    Artist artistMetadata = metadataService.getArtistMetadata(artistQuoteCount.artist());
                    if (artistMetadata == null || artistMetadata.id() == null) {
                        return artistQuoteCount;
                    }

                    logger.debug("syncing metadata for artist {} - {}", artistQuoteCount.artist(), artistMetadata.id());
                    syncArtistMetadataPort.syncArtistMetadata(artistQuoteCount.artist(), artistMetadata);
                    return new ArtistQuoteCount(artistMetadata.id(), artistQuoteCount.artist(), artistQuoteCount.quotes());
                })
                .sorted(java.util.Comparator.comparing(ArtistQuoteCount::quotes).reversed())
                .toList();
    }
}
