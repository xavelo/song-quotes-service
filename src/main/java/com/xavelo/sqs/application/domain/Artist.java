package com.xavelo.sqs.application.domain;

import java.util.List;

public record Artist(
    String id,
    String name,
    List<String> genres,
    int popularity,
    String imageUrl,
    String spotifyUrl,
    List<Track> topTracks
) {
    public record Track(
        String id,
        String name,
        String previewUrl
    ) {}
}