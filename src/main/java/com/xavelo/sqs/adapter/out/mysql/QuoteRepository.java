package com.xavelo.sqs.adapter.out.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.xavelo.sqs.adapter.out.mysql.ArtistQuoteCountView;

public interface QuoteRepository extends JpaRepository<QuoteEntity, Long> {

    /**
     * Retrieve a single random quote entity.
     */
    @Query(value = "SELECT * FROM quotes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    QuoteEntity findRandomQuote();

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.posts = q.posts + 1 where q.id = :id")
    void incrementPosts(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.hits = q.hits + 1 where q.id = :id")
    void incrementHits(@Param("id") Long id);

    /**
     * Retrieve the number of quotes for each artist.
     */
    @Query("SELECT q.artist as artist, COUNT(q) as quotes FROM QuoteEntity q GROUP BY q.artist ORDER BY COUNT(q) DESC")
    List<ArtistQuoteCountView> findArtistQuoteCounts();

    /**
     * Retrieve the top 10 quotes with the most hits.
     */
    @Query("SELECT q FROM QuoteEntity q WHERE q.hits > 0 ORDER BY q.hits DESC")
    List<QuoteEntity> findTop10ByOrderByHitsDesc();
}
