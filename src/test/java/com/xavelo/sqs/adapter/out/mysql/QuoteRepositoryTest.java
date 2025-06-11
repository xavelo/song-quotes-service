package com.xavelo.sqs.adapter.out.mysql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuoteRepositoryTest {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private EntityManager entityManager;

    private QuoteEntity createEntity(String quote, String artist) {
        QuoteEntity e = new QuoteEntity();
        e.setQuote(quote);
        e.setSong("song");
        e.setAlbum("album");
        e.setYear(2024);
        e.setArtist(artist);
        e.setPosts(0);
        e.setHits(0);
        return quoteRepository.save(e);
    }

    //@Test
    void findRandomQuoteReturnsEntity() {
        createEntity("q1", "a1");
        entityManager.flush();

        QuoteEntity result = quoteRepository.findRandomQuote();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }

    //@Test
    void incrementPostsAndHitsUpdatesFields() {
        QuoteEntity saved = createEntity("q2", "a2");
        entityManager.flush();

        quoteRepository.incrementPosts(saved.getId());
        quoteRepository.incrementHits(saved.getId());
        entityManager.flush();

        QuoteEntity updated = quoteRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPosts()).isEqualTo(1);
        assertThat(updated.getHits()).isEqualTo(1);
    }

    //@Test
    void findArtistQuoteCountsReturnsGroupedCounts() {
        createEntity("q1", "ArtistA");
        createEntity("q2", "ArtistA");
        createEntity("q3", "ArtistB");
        entityManager.flush();

        List<ArtistQuoteCountView> counts = quoteRepository.findArtistQuoteCounts();
        Map<String, Long> result = counts.stream()
                .collect(Collectors.toMap(ArtistQuoteCountView::getArtist, ArtistQuoteCountView::getQuotes));

        assertThat(result.get("ArtistA")).isEqualTo(2L);
        assertThat(result.get("ArtistB")).isEqualTo(1L);
    }
}

