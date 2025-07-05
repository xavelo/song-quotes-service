package com.xavelo.sqs.adapter.out.mysql;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.StoreQuotePort;
import com.xavelo.sqs.port.out.QuotesCountPort;
import com.xavelo.sqs.port.out.IncrementPostsPort;
import com.xavelo.sqs.port.out.IncrementHitsPort;
import com.xavelo.sqs.port.out.LoadArtistQuoteCountsPort;
import com.xavelo.sqs.port.out.LoadTop10QuotesPort;
import com.xavelo.sqs.port.out.UpdateQuotePort;
import com.xavelo.sqs.adapter.out.mysql.QuoteMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlAdapter implements StoreQuotePort, LoadQuotePort, DeleteQuotePort, QuotesCountPort, IncrementPostsPort, IncrementHitsPort, LoadArtistQuoteCountsPort, UpdateQuotePort, LoadTop10QuotesPort {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;

    public MysqlAdapter(QuoteRepository quoteRepository, QuoteMapper quoteMapper) {
        this.quoteRepository = quoteRepository;
        this.quoteMapper = quoteMapper;
    }

    public Long storeQuote(Quote quote) {
        QuoteEntity entity = quoteMapper.toEntity(quote);
        QuoteEntity saved = quoteRepository.save(entity);
        return saved.getId();
    }

    @Override
    public java.util.List<Long> storeQuotes(List<Quote> quotes) {
        java.util.List<QuoteEntity> entities = quotes.stream()
                .map(quoteMapper::toEntity)
                .toList();
        java.util.List<QuoteEntity> saved = quoteRepository.saveAll(entities);
        return saved.stream().map(QuoteEntity::getId).toList();
    }

    @Override
    public java.util.List<Quote> loadQuotes() {
        java.util.List<QuoteEntity> entities = quoteRepository.findAll();
        return entities.stream()
                .map(quoteMapper::toDomain)
                .toList();
    }

    @Override
    public Quote loadQuote(Long id) {
        return quoteRepository.findById(id)
                .map(quoteMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Quote loadRandomQuote() {
        QuoteEntity entity = quoteRepository.findRandomQuote();
        if (entity == null) {
            return null;
        }
        return quoteMapper.toDomain(entity);
    }

    @Override
    public void deleteQuote(Long id) {
        quoteRepository.deleteById(id);
    }

    @Override
    public Long countQuotes() {
        return quoteRepository.count();
    }

    @Override
    public void incrementPosts(Long id) {
        quoteRepository.incrementPosts(id);
    }

    @Override
    public void incrementHits(Long id) {
        quoteRepository.incrementHits(id);
    }

    @Override
    public java.util.List<ArtistQuoteCount> loadArtistQuoteCounts() {
        java.util.List<ArtistQuoteCountView> views = quoteRepository.findArtistQuoteCounts();
        return views.stream()
                .map(v -> new ArtistQuoteCount(v.getArtist(), v.getQuotes()))
                .toList();
    }

    @Override
    public void updateQuote(Quote quote) {
        QuoteEntity entity = quoteRepository.findById(quote.id()).orElse(null);
        if (entity != null) {
            entity.setQuote(quote.quote());
            entity.setSong(quote.song());
            entity.setAlbum(quote.album());
            entity.setYear(quote.year());
            entity.setArtist(quote.artist());
            quoteRepository.save(entity);
        }
    }

    @Override
    public List<Quote> loadTop10Quotes() {
        List<QuoteEntity> entities = quoteRepository.findTop10ByOrderByHitsDesc();
        return entities.stream()
                .map(quoteMapper::toDomain)
                .toList();
    }
}
