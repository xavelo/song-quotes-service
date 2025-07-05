package com.xavelo.sqs.application.domain;

import java.util.List;

import java.util.stream.Collectors;

public record Artist(
    String id,
    String name,
    List<String> genres,
    int popularity,
    String imageUrl,
    String spotifyUrl,
    List<Track> topTracks
) {
    @Override
    public String toString() {
        return "Artist{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", genres=" + genres +
            ", popularity=" + popularity +
            ", imageUrl='" + imageUrl + '\'' +
            ", spotifyUrl='" + spotifyUrl + '\'' +
            ", topTracks=" + (topTracks != null ? topTracks.stream().map(Track::toString).collect(Collectors.joining(", ", "[", "]")) : "[]") +
            '}';
    }

    public record Track(
        String id,
        String name,
        String previewUrl
    ) {
        @Override
        public String toString() {
            return "Track{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", previewUrl=" + (previewUrl != null ? '"' + previewUrl + '"' : "null") +
                '}';
        }
    }
}