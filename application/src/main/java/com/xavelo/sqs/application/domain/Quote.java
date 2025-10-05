package com.xavelo.sqs.application.domain;

import java.util.UUID;

public record Quote(
        UUID id,
        String quote,
        String song,
        String album,
        Integer year,
        String artist,
        Integer posts,
        Integer hits,
        String spotifyArtistId
) {}
