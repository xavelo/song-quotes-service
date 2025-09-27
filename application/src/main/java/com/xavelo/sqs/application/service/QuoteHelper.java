package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;

/**
 * Utility methods for manipulating {@link Quote} instances.
 */
public final class QuoteHelper {

    private QuoteHelper() {
    }

    /**
     * Returns a copy of the given quote with the post and hit counters reset to 0.
     */
    public static Quote sanitize(Quote quote) {
        if (quote == null) {
            return null;
        }
        return new Quote(
                quote.id(),
                quote.quote(),
                quote.song(),
                quote.album(),
                quote.year(),
                quote.artist(),
                0,
                0,
                null
        );
    }

    /**
     * Returns a copy of the given quote with the supplied id.
     */
    public static Quote withId(Quote quote, Long id) {
        if (quote == null) {
            return null;
        }
        return new Quote(
                id,
                quote.quote(),
                quote.song(),
                quote.album(),
                quote.year(),
                quote.artist(),
                quote.posts(),
                quote.hits(),
                quote.spotifyArtistId()
        );
    }

    /**
     * Returns a copy of the quote with the posts counter incremented by one.
     */
    public static Quote incrementPosts(Quote quote) {
        if (quote == null) {
            return null;
        }
        int current = quote.posts() != null ? quote.posts() : 0;
        return new Quote(
                quote.id(),
                quote.quote(),
                quote.song(),
                quote.album(),
                quote.year(),
                quote.artist(),
                current + 1,
                quote.hits(),
                quote.spotifyArtistId()
        );
    }

    /**
     * Returns a copy of the quote with the hits counter incremented by one.
     */
    public static Quote incrementHits(Quote quote) {
        if (quote == null) {
            return null;
        }
        int current = quote.hits() != null ? quote.hits() : 0;
        return new Quote(
                quote.id(),
                quote.quote(),
                quote.song(),
                quote.album(),
                quote.year(),
                quote.artist(),
                quote.posts(),
                current + 1,
                quote.spotifyArtistId()
        );
    }
}

