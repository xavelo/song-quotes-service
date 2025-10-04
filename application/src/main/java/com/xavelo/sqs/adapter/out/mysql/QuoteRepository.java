package com.xavelo.sqs.adapter.out.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.xavelo.sqs.adapter.out.mysql.ArtistQuoteCountView;

public interface QuoteRepository extends JpaRepository<QuoteEntity, String> {

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.posts = q.posts + 1 where q.id = :id")
    void incrementPosts(@Param("id") String id);

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.hits = q.hits + 1 where q.id = :id")
    void incrementHits(@Param("id") String id);

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.hits = 0")
    void resetHits();

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.posts = 0")
    void resetPosts();

    /**
     * Retrieve the number of quotes for each artist.
     */
    @Query("SELECT q.spotifyArtistId as id, q.artist as artist, COUNT(q) as quotes FROM QuoteEntity q GROUP BY q.spotifyArtistId, q.artist ORDER BY COUNT(q) DESC")
    List<ArtistQuoteCountView> findArtistQuoteCounts();

    @Transactional
    @Modifying
    @Query("update QuoteEntity q set q.spotifyArtistId = :spotifyArtistId where q.artist = :artist and (q.spotifyArtistId is null or q.spotifyArtistId = '')")
    void assignSpotifyArtistId(@Param("artist") String artist, @Param("spotifyArtistId") String spotifyArtistId);

    /**
     * Retrieve the top 10 quotes with the most hits.
     */
    @Query("SELECT q FROM QuoteEntity q WHERE q.hits > 0 ORDER BY q.hits DESC")
    List<QuoteEntity> findTop10ByOrderByHitsDesc();
}
