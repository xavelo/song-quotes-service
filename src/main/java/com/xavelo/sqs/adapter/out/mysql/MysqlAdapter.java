package com.xavelo.sqs.adapter.out.mysql;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.DeleteQuotePort;
import com.xavelo.sqs.port.out.LoadQuotePort;
import com.xavelo.sqs.port.out.StoreQuotePort;
import com.xavelo.sqs.port.out.QuotesCountPort;
import com.xavelo.sqs.port.out.IncrementPostsPort;
import com.xavelo.sqs.port.out.IncrementHitsPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlAdapter implements StoreQuotePort, LoadQuotePort, DeleteQuotePort, QuotesCountPort, IncrementPostsPort, IncrementHitsPort {

    private final QuoteRepository quoteRepository;

    public MysqlAdapter(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public Long storeQuote(Quote quote) {
        QuoteEntity entity = new QuoteEntity();
        entity.setQuote(quote.quote());
        entity.setSong(quote.song());
        entity.setAlbum(quote.album());
        entity.setYear(quote.year());
        entity.setArtist(quote.artist());
        entity.setPosts(0);
        entity.setHits(0);
        QuoteEntity saved = quoteRepository.save(entity);
        return saved.getId();
    }

    @Override
    public java.util.List<Long> storeQuotes(List<Quote> quotes) {
        java.util.List<QuoteEntity> entities = quotes.stream().map(q -> {
            QuoteEntity e = new QuoteEntity();
            e.setQuote(q.quote());
            e.setSong(q.song());
            e.setAlbum(q.album());
            e.setYear(q.year());
            e.setArtist(q.artist());
            e.setPosts(0);
            e.setHits(0);
            return e;
        }).toList();
        java.util.List<QuoteEntity> saved = quoteRepository.saveAll(entities);
        return saved.stream().map(QuoteEntity::getId).toList();
    }

    @Override
    public java.util.List<Quote> loadQuotes() {
        java.util.List<QuoteEntity> entities = quoteRepository.findAll();
        return entities.stream()
                .map(e -> new Quote(e.getId(), e.getQuote(), e.getSong(), e.getAlbum(), e.getYear(), e.getArtist(), e.getPosts(), e.getHits()))
                .toList();
    }

    @Override
    public Quote loadQuote(Long id) {
        return quoteRepository.findById(id)
                .map(e -> new Quote(e.getId(), e.getQuote(), e.getSong(), e.getAlbum(), e.getYear(), e.getArtist(), e.getPosts(), e.getHits()))
                .orElse(null);
    }

    @Override
    public Quote loadRandomQuote() {
        QuoteEntity entity = quoteRepository.findRandomQuote();
        if (entity == null) {
            return null;
        }
        return new Quote(entity.getId(), entity.getQuote(), entity.getSong(), entity.getAlbum(), entity.getYear(), entity.getArtist(), entity.getPosts(), entity.getHits());
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
}
