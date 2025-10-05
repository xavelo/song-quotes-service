package com.xavelo.sqs.application.domain;

public record Quote(
        String id,
        String quote,
        String song,
        String album,
        Integer year,
        String artist,
        Integer posts,
        Integer hits,
        String spotifyArtistId
) {}
