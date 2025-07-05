package com.xavelo.sqs.adapter.out.mongo;

import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.out.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

@Component("mongoAdapter")
public class MongoAdapter implements StoreQuotePort, LoadQuotePort, DeleteQuotePort, QuotesCountPort, IncrementPostsPort, IncrementHitsPort, LoadArtistQuoteCountsPort, UpdateQuotePort, LoadTop10QuotesPort, PatchQuotePort {

    private final QuoteMongoRepository quoteMongoRepository;
    private final QuoteMongoMapper quoteMongoMapper;

    public MongoAdapter(QuoteMongoRepository quoteMongoRepository, QuoteMongoMapper quoteMongoMapper) {
        this.quoteMongoRepository = quoteMongoRepository;
        this.quoteMongoMapper = quoteMongoMapper;
    }

    @Override
    public Long storeQuote(Quote quote) {
        QuoteMongoEntity entity = quoteMongoMapper.toEntity(quote);
        QuoteMongoEntity saved = quoteMongoRepository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> storeQuotes(List<Quote> quotes) {
        List<QuoteMongoEntity> entities = quotes.stream()
                .map(quoteMongoMapper::toEntity)
                .toList();
        List<QuoteMongoEntity> saved = quoteMongoRepository.saveAll(entities);
        return saved.stream().map(QuoteMongoEntity::getId).toList();
    }

    @Override
    public List<Quote> loadQuotes() {
        List<QuoteMongoEntity> entities = quoteMongoRepository.findAll();
        return entities.stream()
                .map(quoteMongoMapper::toDomain)
                .toList();
    }

    @Override
    public Quote loadQuote(Long id) {
        return quoteMongoRepository.findById(id)
                .map(quoteMongoMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Quote loadRandomQuote() {
        // MongoDB doesn't have a direct random function like MySQL, so we'll fetch all and pick one randomly
        List<QuoteMongoEntity> allQuotes = quoteMongoRepository.findAll();
        if (allQuotes.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * allQuotes.size());
        return quoteMongoMapper.toDomain(allQuotes.get(randomIndex));
    }

    @Override
    public void deleteQuote(Long id) {
        quoteMongoRepository.deleteById(id);
    }

    @Override
    public Long countQuotes() {
        return quoteMongoRepository.count();
    }

    @Override
    public void incrementPosts(Long id) {
        Optional<QuoteMongoEntity> optionalEntity = quoteMongoRepository.findById(id);
        optionalEntity.ifPresent(entity -> {
            entity.setPosts(entity.getPosts() + 1);
            quoteMongoRepository.save(entity);
        });
    }

    @Override
    public void incrementHits(Long id) {
        Optional<QuoteMongoEntity> optionalEntity = quoteMongoRepository.findById(id);
        optionalEntity.ifPresent(entity -> {
            entity.setHits(entity.getHits() + 1);
            quoteMongoRepository.save(entity);
        });
    }

    @Override
    public List<ArtistQuoteCount> loadArtistQuoteCounts() {
        // This would require an aggregation pipeline in MongoDB, simplifying for now
        return List.of();
    }

    @Override
    public void updateQuote(Quote quote) {
        Optional<QuoteMongoEntity> optionalEntity = quoteMongoRepository.findById(quote.id());
        optionalEntity.ifPresent(entity -> {
            entity.setQuote(quote.quote());
            entity.setSong(quote.song());
            entity.setAlbum(quote.album());
            entity.setYear(quote.year());
            entity.setArtist(quote.artist());
            entity.setHits(quote.hits());
            entity.setPosts(quote.posts());
            quoteMongoRepository.save(entity);
        });
    }

    @Override
    public List<Quote> loadTop10Quotes() {
        List<QuoteMongoEntity> entities = quoteMongoRepository.findTop10ByOrderByHitsDesc();
        return entities.stream()
                .map(quoteMongoMapper::toDomain)
                .toList();
    }

    @Override
    public void patchQuote(Long id, Quote quote) {
        Optional<QuoteMongoEntity> optionalEntity = quoteMongoRepository.findById(id);
        optionalEntity.ifPresent(entity -> {
            if (quote.quote() != null) {
                entity.setQuote(quote.quote());
            }
            if (quote.song() != null) {
                entity.setSong(quote.song());
            }
            if (quote.album() != null) {
                entity.setAlbum(quote.album());
            }
            if (quote.year() != null) {
                entity.setYear(quote.year());
            }
            if (quote.artist() != null) {
                entity.setArtist(quote.artist());
            }
            if (quote.hits() != null) {
                entity.setHits(quote.hits());
            }
            if (quote.posts() != null) {
                entity.setPosts(quote.posts());
            }
            quoteMongoRepository.save(entity);
        });
    }
}
