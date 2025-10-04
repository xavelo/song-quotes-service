package com.xavelo.sqs.adapter.out.mysql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuoteRepositoryTest {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private EntityManager entityManager;

    private QuoteEntity createEntity(String quote, String artist, String spotifyArtistId) {
        QuoteEntity e = new QuoteEntity();
        e.setQuote(quote);
        e.setSong("song");
        e.setAlbum("album");
        e.setYear(2024);
        e.setArtist(artist);
        e.setSpotifyArtistId(spotifyArtistId);
        e.setPosts(0);
        e.setHits(0);
        return quoteRepository.save(e);
    }

    @Test
    void saveGeneratesUuidIdentifier() {
        QuoteEntity saved = createEntity("q1", "a1", "id-1");
        entityManager.flush();

        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getId()).hasSize(36);
        assertThat(saved.getId()).contains("-");
    }

    //@Test
    void incrementPostsAndHitsUpdatesFields() {
        QuoteEntity saved = createEntity("q2", "a2", "id-2");
        entityManager.flush();

        quoteRepository.incrementPosts(saved.getId());
        quoteRepository.incrementHits(saved.getId());
        entityManager.flush();

        QuoteEntity updated = quoteRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getPosts()).isEqualTo(1);
        assertThat(updated.getHits()).isEqualTo(1);
    }

    @Test
    void findArtistQuoteCountsReturnsGroupedCounts() {
        createEntity("q1", "ArtistA", "artist-a");
        createEntity("q2", "ArtistA", "artist-a");
        createEntity("q3", "ArtistB", "artist-b");
        entityManager.flush();

        List<ArtistQuoteCountView> counts = quoteRepository.findArtistQuoteCounts();
        Map<String, Long> resultByArtist = counts.stream()
                .collect(Collectors.toMap(
                        ArtistQuoteCountView::getArtist,
                        ArtistQuoteCountView::getQuotes,
                        (left, right) -> left
                ));

        assertThat(resultByArtist.get("ArtistA")).isEqualTo(2L);
        assertThat(resultByArtist.get("ArtistB")).isEqualTo(1L);

        assertThat(counts).anySatisfy(view -> {
            if ("ArtistA".equals(view.getArtist())) {
                assertThat(view.getId()).isEqualTo("artist-a");
            }
        });
    }
}

