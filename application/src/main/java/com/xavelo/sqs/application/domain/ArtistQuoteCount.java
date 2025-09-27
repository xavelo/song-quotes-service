package com.xavelo.sqs.application.domain;

/**
 * Represents an artist with the number of quotes stored for them.
 */
public record ArtistQuoteCount(String id, String artist, Long quotes) {}
