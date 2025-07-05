package com.xavelo.sqs.adapter.out.mongo;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoAdapterTest {

    @Mock
    private QuoteMongoRepository quoteMongoRepository;

    @Mock
    private QuoteMongoMapper quoteMongoMapper;

    @InjectMocks
    private MongoAdapter mongoAdapter;

    private Quote quote;
    private QuoteMongoEntity quoteMongoEntity;

    @BeforeEach
    void setUp() {
        quote = new Quote(1L, "Test Quote", "Test Song", "Test Album", 2023, "Test Artist", 0, 0);
        quoteMongoEntity = new QuoteMongoEntity(1L, "Test Quote", "Test Song", "Test Album", 2023, "Test Artist", 0, 0);
    }

    @Test
    void storeQuote() {
        when(quoteMongoMapper.toEntity(quote)).thenReturn(quoteMongoEntity);
        when(quoteMongoRepository.save(quoteMongoEntity)).thenReturn(quoteMongoEntity);

        Long id = mongoAdapter.storeQuote(quote);

        assertEquals(1L, id);
        verify(quoteMongoMapper).toEntity(quote);
        verify(quoteMongoRepository).save(quoteMongoEntity);
    }

    @Test
    void storeQuotes() {
        List<Quote> quotes = Arrays.asList(quote);
        List<QuoteMongoEntity> entities = Arrays.asList(quoteMongoEntity);

        when(quoteMongoMapper.toEntity(any(Quote.class))).thenReturn(quoteMongoEntity);
        when(quoteMongoRepository.saveAll(anyList())).thenReturn(entities);

        List<Long> ids = mongoAdapter.storeQuotes(quotes);

        assertEquals(1, ids.size());
        assertEquals(1L, ids.get(0));
        verify(quoteMongoMapper, times(1)).toEntity(any(Quote.class));
        verify(quoteMongoRepository).saveAll(anyList());
    }

    @Test
    void loadQuotes() {
        List<QuoteMongoEntity> entities = Arrays.asList(quoteMongoEntity);
        when(quoteMongoRepository.findAll()).thenReturn(entities);
        when(quoteMongoMapper.toDomain(any(QuoteMongoEntity.class))).thenReturn(quote);

        List<Quote> quotes = mongoAdapter.loadQuotes();

        assertEquals(1, quotes.size());
        assertEquals(quote, quotes.get(0));
        verify(quoteMongoRepository).findAll();
        verify(quoteMongoMapper, times(1)).toDomain(any(QuoteMongoEntity.class));
    }

    @Test
    void loadQuote() {
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.of(quoteMongoEntity));
        when(quoteMongoMapper.toDomain(quoteMongoEntity)).thenReturn(quote);

        Quote result = mongoAdapter.loadQuote(1L);

        assertEquals(quote, result);
        verify(quoteMongoRepository).findById(1L);
        verify(quoteMongoMapper).toDomain(quoteMongoEntity);
    }

    @Test
    void loadQuoteNotFound() {
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.empty());

        Quote result = mongoAdapter.loadQuote(1L);

        assertNull(result);
        verify(quoteMongoRepository).findById(1L);
        verify(quoteMongoMapper, never()).toDomain(any(QuoteMongoEntity.class));
    }

    @Test
    void loadRandomQuote() {
        List<QuoteMongoEntity> allQuotes = Arrays.asList(quoteMongoEntity);
        when(quoteMongoRepository.findAll()).thenReturn(allQuotes);
        when(quoteMongoMapper.toDomain(any(QuoteMongoEntity.class))).thenReturn(quote);

        Quote result = mongoAdapter.loadRandomQuote();

        assertEquals(quote, result);
        verify(quoteMongoRepository).findAll();
        verify(quoteMongoMapper).toDomain(any(QuoteMongoEntity.class));
    }

    @Test
    void loadRandomQuoteEmpty() {
        when(quoteMongoRepository.findAll()).thenReturn(List.of());

        Quote result = mongoAdapter.loadRandomQuote();

        assertNull(result);
        verify(quoteMongoRepository).findAll();
        verify(quoteMongoMapper, never()).toDomain(any(QuoteMongoEntity.class));
    }

    @Test
    void deleteQuote() {
        mongoAdapter.deleteQuote(1L);
        verify(quoteMongoRepository).deleteById(1L);
    }

    @Test
    void countQuotes() {
        when(quoteMongoRepository.count()).thenReturn(5L);
        Long count = mongoAdapter.countQuotes();
        assertEquals(5L, count);
        verify(quoteMongoRepository).count();
    }

    @Test
    void incrementPosts() {
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.of(quoteMongoEntity));
        mongoAdapter.incrementPosts(1L);
        assertEquals(1, quoteMongoEntity.getPosts());
        verify(quoteMongoRepository).findById(1L);
        verify(quoteMongoRepository).save(quoteMongoEntity);
    }

    @Test
    void incrementHits() {
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.of(quoteMongoEntity));
        mongoAdapter.incrementHits(1L);
        assertEquals(1, quoteMongoEntity.getHits());
        verify(quoteMongoRepository).findById(1L);
        verify(quoteMongoRepository).save(quoteMongoEntity);
    }

    @Test
    void loadArtistQuoteCounts() {
        List<ArtistQuoteCount> result = mongoAdapter.loadArtistQuoteCounts();
        assertTrue(result.isEmpty());
    }

    @Test
    void updateQuote() {
        Quote updatedQuote = new Quote(1L, "Updated Quote", "Updated Song", "Updated Album", 2024, "Updated Artist", 10, 5);
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.of(quoteMongoEntity));

        mongoAdapter.updateQuote(updatedQuote);

        assertEquals("Updated Quote", quoteMongoEntity.getQuote());
        assertEquals("Updated Song", quoteMongoEntity.getSong());
        assertEquals("Updated Album", quoteMongoEntity.getAlbum());
        assertEquals(2024, quoteMongoEntity.getYear());
        assertEquals("Updated Artist", quoteMongoEntity.getArtist());
        verify(quoteMongoRepository).save(quoteMongoEntity);
    }

    @Test
    void loadTop10Quotes() {
        List<QuoteMongoEntity> entities = Arrays.asList(quoteMongoEntity);
        when(quoteMongoRepository.findTop10ByOrderByHitsDesc()).thenReturn(entities);
        when(quoteMongoMapper.toDomain(any(QuoteMongoEntity.class))).thenReturn(quote);

        List<Quote> quotes = mongoAdapter.loadTop10Quotes();

        assertEquals(1, quotes.size());
        assertEquals(quote, quotes.get(0));
        verify(quoteMongoRepository).findTop10ByOrderByHitsDesc();
        verify(quoteMongoMapper, times(1)).toDomain(any(QuoteMongoEntity.class));
    }

    @Test
    void patchQuote() {
        Quote patch = new Quote(null, "Patched Quote", null, null, null, null, null, null);
        when(quoteMongoRepository.findById(1L)).thenReturn(Optional.of(quoteMongoEntity));

        mongoAdapter.patchQuote(1L, patch);

        assertEquals("Patched Quote", quoteMongoEntity.getQuote());
        assertEquals("Test Song", quoteMongoEntity.getSong()); // Should remain unchanged
        verify(quoteMongoRepository).findById(1L);
        verify(quoteMongoRepository).save(quoteMongoEntity);
    }
}
