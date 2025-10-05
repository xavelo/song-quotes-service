package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Quote;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuoteHelperTest {

    @Test
    void sanitizeForExportRemovesRestrictedFieldsWhileKeepingContent() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Quote quote = new Quote(id, "quote", "song", "album", 1999, "artist", 5, 7, "spotify-id");

        Quote sanitized = QuoteHelper.sanitizeForExport(quote);

        assertNull(sanitized.id());
        assertNull(sanitized.posts());
        assertNull(sanitized.hits());
        assertNull(sanitized.spotifyArtistId());
        assertEquals("quote", sanitized.quote());
        assertEquals("song", sanitized.song());
        assertEquals("album", sanitized.album());
        assertEquals(1999, sanitized.year());
        assertEquals("artist", sanitized.artist());
    }

    @Test
    void sanitizeForExportReturnsNullWhenQuoteIsNull() {
        assertNull(QuoteHelper.sanitizeForExport(null));
    }
}
