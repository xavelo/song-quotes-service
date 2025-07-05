package com.xavelo.sqs.adapter.out.spotify.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifyTopTracksResponse(
    List<Track> tracks
) {
    public record Track(
        String id,
        String name,
        int popularity,
        @JsonProperty("preview_url") String previewUrl,
        Album album,
        List<ArtistItem> artists
    ) {}

    public record Album(
        String id,
        String name,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("release_date_precision") String releaseDatePrecision
    ) {}

    public record ArtistItem(
        String id,
        String name
    ) {}
}
