package com.xavelo.sqs.adapter.out.mysql;

/**
 * Projection for artist quote counts.
 */
public interface ArtistQuoteCountView {
    String getArtist();
    Long getQuotes();
}
