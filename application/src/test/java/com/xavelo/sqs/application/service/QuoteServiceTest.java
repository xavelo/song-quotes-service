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
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    private static final UUID QUOTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

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
    private ArgumentCaptor<List<Artist>> artistListCaptor;
    @Captor
    private ArgumentCaptor<Quote> publishedQuoteCaptor;
    @Captor
    private ArgumentCaptor<Object> eventCaptor;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        sampleQuote = new Quote(QUOTE_ID, "q", "s", "a", 2000, "artist", 5, 7, null);
    }

    @Test
    void storeQuote_sanitizesAndDelegates() {
        UUID generatedId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(storeQuotePort.storeQuote(any(Quote.class), any(Artist.class))).thenReturn(generatedId);
        when(metadataService.getArtistMetadata(anyString()))
                .thenReturn(new Artist("id", "name", List.of(), 0, "imageUrl", "spotifyUrl", List.of()));

        UUID id = quoteService.storeQuote(sampleQuote);

        verify(storeQuotePort).storeQuote(quoteCaptor.capture(), any(Artist.class));
        Quote sent = quoteCaptor.getValue();
        assertEquals(0, sent.posts());
        assertEquals(0, sent.hits());
        verify(quoteEventOutboxPort).recordQuoteCreatedEvent(publishedQuoteCaptor.capture());
        Quote published = publishedQuoteCaptor.getValue();
        assertEquals(generatedId, published.id());
        assertEquals(sent.quote(), published.quote());
        assertEquals(generatedId, id);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof QuoteStoredEvent);
        assertEquals(generatedId, ((QuoteStoredEvent) event).quote().id());
    }

    @Test
    void storeQuotes_sanitizesAllQuotesAndDelegates() {
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111112");
        UUID id2 = UUID.fromString("11111111-1111-1111-1111-111111111113");
        when(storeQuotePort.storeQuotes(any(), any())).thenReturn(List.of(id1, id2));

        Artist metadata1 = new Artist("spotify-artist-1", "artist", List.of(), 10, "image1", "url1", List.of());
        Artist metadata2 = new Artist("spotify-artist-2", "artist2", List.of(), 20, "image2", "url2", List.of());
        when(metadataService.getArtistMetadata("artist")).thenReturn(metadata1);
        when(metadataService.getArtistMetadata("artist2")).thenReturn(metadata2);

        Quote q2 = new Quote(UUID.fromString("22222222-2222-2222-2222-222222222222"), "q2", "s2", "a2", 1999, "artist2", 3, 4, null);
        List<UUID> ids = quoteService.storeQuotes(List.of(sampleQuote, q2));

        verify(storeQuotePort).storeQuotes(quoteListCaptor.capture(), artistListCaptor.capture());
        List<Quote> sent = quoteListCaptor.getValue();
        assertEquals(2, sent.size());
        for (Quote q : sent) {
            assertEquals(0, q.posts());
            assertEquals(0, q.hits());
        }
        List<Artist> sentMetadata = artistListCaptor.getValue();
        assertEquals(List.of(metadata1, metadata2), sentMetadata);
        verify(quoteEventOutboxPort, times(2)).recordQuoteCreatedEvent(publishedQuoteCaptor.capture());
        List<Quote> published = publishedQuoteCaptor.getAllValues();
        assertEquals(2, published.size());
        assertEquals("spotify-artist-1", published.get(0).spotifyArtistId());
        assertEquals("spotify-artist-2", published.get(1).spotifyArtistId());
        assertEquals(List.of(id1, id2), ids);
        verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());
        List<Object> events = eventCaptor.getAllValues();
        assertEquals(2, events.size());
        assertTrue(events.stream().allMatch(QuoteStoredEvent.class::isInstance));
    }

    @Test
    void getQuote_incrementsHitsAndReturnsUpdatedQuote() {
        when(loadQuotePort.loadQuote(QUOTE_ID)).thenReturn(sampleQuote);

        Quote result = quoteService.getQuote(QUOTE_ID);

        verify(incrementHitsPort).incrementHits(QUOTE_ID);
        verify(quoteEventOutboxPort).recordQuoteHitEvent(result);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertTrue(event instanceof QuoteHitEvent);
        assertEquals(QUOTE_ID, ((QuoteHitEvent) event).quote().id());
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