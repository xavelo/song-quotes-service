package com.xavelo.sqs.adapter.out.spotify.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifySearchResponse(
    List<ArtistItem> artists
) {
    public record ArtistItem(
        String id,
        String name,
        @JsonProperty("external_urls") ExternalUrls externalUrls,
        List<String> genres,
        int popularity
    ) {}

    public record ExternalUrls(
        String spotify
    ) {}
}
