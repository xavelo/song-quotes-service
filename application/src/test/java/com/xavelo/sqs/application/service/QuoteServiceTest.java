package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.event.QuoteHitEvent;
import com.xavelo.sqs.application.service.event.QuoteStoredEvent;
import com.xavelo.sqs.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private StoreQuotePort storeQuotePort;
    @Mock
    private LoadQuotePort loadQuotePort;
    @Mock
    private QuotesCountPort quotesCountPort;
    @Mock
    private DeleteQuotePort deleteQuotePort;
    @Mock
    private IncrementPostsPort incrementPostsPort;
    @Mock
    private IncrementHitsPort incrementHitsPort;
    @Mock
    private LoadArtistQuoteCountsPort loadArtistQuoteCountsPort;
    @Mock
    private UpdateQuotePort updateQuotePort;
    @Mock
    private QuoteEventOutboxPort quoteEventOutboxPort;
    @Mock
    private MetadataService metadataService;
    @Mock
    private SyncArtistMetadataPort syncArtistMetadataPort;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private QuoteService quoteService;

    @Captor
    private ArgumentCaptor<Quote> quoteCaptor;
    @Captor
    private ArgumentCaptor<List<Quote>> quoteListCaptor;
    @Captor
    private ArgumentCaptor<Quote> publishedQuoteCaptor;
    @Captor
    private ArgumentCaptor<Object> eventCaptor;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        sampleQuote = new Quote("quote-id-1", "q", "s", "a", 2000, "artist", 5, 7, null);
    }

    @Test
    void storeQuote_sanitizesAndDelegates() {
        when(storeQuotePort.storeQuote(any(Quote.class), any(Artist.class))).thenReturn("generated-id");
        when(metadataService.getArtistMetadata(anyString()))
                .thenReturn(new Artist("id", "name", List.of(), 0, "imageUrl", "spotifyUrl", List.of()));

        String id = quoteService.storeQuote(sampleQuote);

        verify(storeQuotePort).storeQuote(quoteCaptor.capture(), any(Artist.class));
        Quote sent = quoteCaptor.getValue();
        assertEquals(0, sent.posts());
        assertEquals(0, sent.hits());
        verify(quoteEventOutboxPort).recordQuoteCreatedEvent(publishedQuoteCaptor.capture());
        Quote published = publishedQuoteCaptor.getValue();
        assertEquals("generated-id", published.id());
        assertEquals(sent.quote(), published.quote());
        assertEquals("generated-id", id);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof QuoteStoredEvent);
        assertEquals("generated-id", ((QuoteStoredEvent) event).quote().id());
    }

    @Test
    void storeQuotes_sanitizesAllQuotesAndDelegates() {
        when(storeQuotePort.storeQuotes(any())).thenReturn(List.of("quote-id-1", "quote-id-2"));

        Quote q2 = new Quote("quote-id-2", "q2", "s2", "a2", 1999, "artist2", 3, 4, null);
        List<String> ids = quoteService.storeQuotes(List.of(sampleQuote, q2));

        verify(storeQuotePort).storeQuotes(quoteListCaptor.capture());
        List<Quote> sent = quoteListCaptor.getValue();
        assertEquals(2, sent.size());
        for (Quote q : sent) {
            assertEquals(0, q.posts());
            assertEquals(0, q.hits());
        }
        verify(quoteEventOutboxPort, times(2)).recordQuoteCreatedEvent(publishedQuoteCaptor.capture());
        List<Quote> published = publishedQuoteCaptor.getAllValues();
        assertEquals(2, published.size());
        assertEquals(List.of("quote-id-1", "quote-id-2"), ids);
        verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
        List<Object> events = eventCaptor.getAllValues();
        assertEquals(2, events.size());
        assertTrue(events.stream().allMatch(QuoteStoredEvent.class::isInstance));
    }

    @Test
    void getQuote_incrementsHitsAndReturnsUpdatedQuote() {
        when(loadQuotePort.loadQuote("quote-id-1")).thenReturn(sampleQuote);

        Quote result = quoteService.getQuote("quote-id-1");

        verify(incrementHitsPort).incrementHits("quote-id-1");
        verify(quoteEventOutboxPort).recordQuoteHitEvent(result);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof QuoteHitEvent);
        assertEquals("quote-id-1", ((QuoteHitEvent) event).quote().id());
        assertNotNull(result);
        assertEquals(Integer.valueOf(sampleQuote.hits() + 1), result.hits());
        assertEquals(sampleQuote.posts(), result.posts());
        assertEquals(sampleQuote.id(), result.id());
        assertEquals(sampleQuote.quote(), result.quote());
        assertEquals(sampleQuote.song(), result.song());
        assertEquals(sampleQuote.album(), result.album());
        assertEquals(sampleQuote.year(), result.year());
        assertEquals(sampleQuote.artist(), result.artist());
    }

    @Test
    void getRandomQuote_incrementsHitsAndReturnsUpdatedQuote() {
        when(loadQuotePort.loadRandomQuote()).thenReturn(sampleQuote);

        Quote result = quoteService.getRandomQuote();

        verify(incrementHitsPort).incrementHits(sampleQuote.id());
        verify(quoteEventOutboxPort).recordQuoteHitEvent(result);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof QuoteHitEvent);
        assertEquals(sampleQuote.id(), ((QuoteHitEvent) event).quote().id());
        assertNotNull(result);
        assertEquals(Integer.valueOf(sampleQuote.hits() + 1), result.hits());
        assertEquals(sampleQuote.posts(), result.posts());
        assertEquals(sampleQuote.id(), result.id());
        assertEquals(sampleQuote.quote(), result.quote());
        assertEquals(sampleQuote.song(), result.song());
        assertEquals(sampleQuote.album(), result.album());
        assertEquals(sampleQuote.year(), result.year());
        assertEquals(sampleQuote.artist(), result.artist());
    }

    @Test
    void countQuotes_delegatesToPort() {
        when(quotesCountPort.countQuotes()).thenReturn(3L);
        Long result = quoteService.countQuotes();
        assertEquals(3L, result);
        verify(quotesCountPort).countQuotes();
    }

    @Test
    void getArtistQuoteCounts_delegatesToPort() {
        List<ArtistQuoteCount> expected = List.of(new ArtistQuoteCount("id-a", "a", 2L));
        when(loadArtistQuoteCountsPort.loadArtistQuoteCounts()).thenReturn(expected);

        List<ArtistQuoteCount> result = quoteService.getArtistQuoteCounts();

        assertEquals(expected, result);
        verify(loadArtistQuoteCountsPort).loadArtistQuoteCounts();
    }

    @Test
    void getArtistQuoteCounts_sortsByQuotesDescending() {
        List<ArtistQuoteCount> unsorted = List.of(
                new ArtistQuoteCount("id-a", "a", 1L),
                new ArtistQuoteCount("id-b", "b", 3L),
                new ArtistQuoteCount("id-c", "c", 2L)
        );
        when(loadArtistQuoteCountsPort.loadArtistQuoteCounts()).thenReturn(unsorted);

        List<ArtistQuoteCount> result = quoteService.getArtistQuoteCounts();

        List<ArtistQuoteCount> expected = List.of(
                new ArtistQuoteCount("id-b", "b", 3L),
                new ArtistQuoteCount("id-c", "c", 2L),
                new ArtistQuoteCount("id-a", "a", 1L)
        );
        assertEquals(expected, result);
    }

    @Test
    void getArtistQuoteCounts_fetchesMissingMetadata() {
        List<ArtistQuoteCount> countsWithNullId = List.of(new ArtistQuoteCount(null, "ArtistA", 5L));
        when(loadArtistQuoteCountsPort.loadArtistQuoteCounts()).thenReturn(countsWithNullId);

        Artist metadata = new Artist("spotify-artist", "ArtistA", List.of("rock"), 10, "image", "url", List.of());
        when(metadataService.getArtistMetadata("ArtistA")).thenReturn(metadata);

        List<ArtistQuoteCount> result = quoteService.getArtistQuoteCounts();

        assertEquals(1, result.size());
        ArtistQuoteCount enriched = result.get(0);
        assertEquals("spotify-artist", enriched.id());
        assertEquals(5L, enriched.quotes());
        verify(syncArtistMetadataPort).syncArtistMetadata("ArtistA", metadata);
    }
}