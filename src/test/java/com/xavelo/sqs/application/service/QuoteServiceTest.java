package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private MetricsPort metricsPort;
    @Mock
    private LoadArtistQuoteCountsPort loadArtistQuoteCountsPort;
    @Mock
    private UpdateQuotePort updateQuotePort;
    @Mock
    private PublishQuoteCreatedPort publishQuoteCreatedPort;
    @Mock
    private LoadTop10QuotesPort loadTop10QuotesPort;
    @Mock
    private MetadataService metadataService;

    @InjectMocks
    private QuoteService quoteService;

    @Captor
    private ArgumentCaptor<Quote> quoteCaptor;
    @Captor
    private ArgumentCaptor<List<Quote>> quoteListCaptor;
    @Captor
    private ArgumentCaptor<Quote> publishedQuoteCaptor;

    private Quote sampleQuote;

    @BeforeEach
    void setUp() {
        sampleQuote = new Quote(1L, "q", "s", "a", 2000, "artist", 5, 7, null);
    }

    @Test
    void storeQuote_sanitizesAndDelegates() {
        when(storeQuotePort.storeQuote(any(Quote.class), any(Artist.class))).thenReturn(10L);
        when(metadataService.getArtistMetadata(anyString()))
                .thenReturn(new Artist("id", "name", List.of(), 0, "imageUrl", "spotifyUrl", List.of()));

        Long id = quoteService.storeQuote(sampleQuote);

        verify(storeQuotePort).storeQuote(quoteCaptor.capture(), any(Artist.class));
        Quote sent = quoteCaptor.getValue();
        assertEquals(0, sent.posts());
        assertEquals(0, sent.hits());
        verify(publishQuoteCreatedPort).publishQuoteCreated(publishedQuoteCaptor.capture());
        Quote published = publishedQuoteCaptor.getValue();
        assertEquals(10L, published.id());
        assertEquals(sent.quote(), published.quote());
        assertEquals(10L, id);
    }

    @Test
    void storeQuotes_sanitizesAllQuotesAndDelegates() {
        when(storeQuotePort.storeQuotes(any())).thenReturn(List.of(1L, 2L));

        Quote q2 = new Quote(2L, "q2", "s2", "a2", 1999, "artist2", 3, 4, null);
        List<Long> ids = quoteService.storeQuotes(List.of(sampleQuote, q2));

        verify(storeQuotePort).storeQuotes(quoteListCaptor.capture());
        List<Quote> sent = quoteListCaptor.getValue();
        assertEquals(2, sent.size());
        for (Quote q : sent) {
            assertEquals(0, q.posts());
            assertEquals(0, q.hits());
        }
        verify(publishQuoteCreatedPort, times(2)).publishQuoteCreated(publishedQuoteCaptor.capture());
        List<Quote> published = publishedQuoteCaptor.getAllValues();
        assertEquals(2, published.size());
        assertEquals(List.of(1L, 2L), ids);
    }

    @Test
    void getQuote_incrementsHitsAndReturnsUpdatedQuote() {
        when(loadQuotePort.loadQuote(1L)).thenReturn(sampleQuote);

        Quote result = quoteService.getQuote(1L);

        verify(incrementHitsPort).incrementHits(1L);
        verify(metricsPort).incrementHits();
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
    void getRandomQuote_incrementsPostsAndReturnsUpdatedQuote() {
        when(loadQuotePort.loadRandomQuote()).thenReturn(sampleQuote);

        Quote result = quoteService.getRandomQuote();

        verify(incrementPostsPort).incrementPosts(sampleQuote.id());
        assertNotNull(result);
        assertEquals(Integer.valueOf(sampleQuote.posts() + 1), result.posts());
        assertEquals(sampleQuote.hits(), result.hits());
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
        List<ArtistQuoteCount> expected = List.of(new ArtistQuoteCount("a", 2L));
        when(loadArtistQuoteCountsPort.loadArtistQuoteCounts()).thenReturn(expected);

        List<ArtistQuoteCount> result = quoteService.getArtistQuoteCounts();

        assertEquals(expected, result);
        verify(loadArtistQuoteCountsPort).loadArtistQuoteCounts();
    }

    @Test
    void getArtistQuoteCounts_sortsByQuotesDescending() {
        List<ArtistQuoteCount> unsorted = List.of(
                new ArtistQuoteCount("a", 1L),
                new ArtistQuoteCount("b", 3L),
                new ArtistQuoteCount("c", 2L)
        );
        when(loadArtistQuoteCountsPort.loadArtistQuoteCounts()).thenReturn(unsorted);

        List<ArtistQuoteCount> result = quoteService.getArtistQuoteCounts();

        List<ArtistQuoteCount> expected = List.of(
                new ArtistQuoteCount("b", 3L),
                new ArtistQuoteCount("c", 2L),
                new ArtistQuoteCount("a", 1L)
        );
        assertEquals(expected, result);
    }

    @Test
    void getTop10Quotes_delegatesToPort() {
        List<Quote> expected = List.of(sampleQuote);
        when(loadTop10QuotesPort.loadTop10Quotes()).thenReturn(expected);

        List<Quote> result = quoteService.getTop10Quotes();

        assertEquals(expected, result);
        verify(loadTop10QuotesPort).loadTop10Quotes();
    }
}
