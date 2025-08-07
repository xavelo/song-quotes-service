package com.xavelo.sqs.adapter.out.spotify.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SpotifyArtistResponse(
    String id,
    String name,
    List<String> genres,
    int popularity,
    List<Image> images,
    @JsonProperty("external_urls") ExternalUrls externalUrls
) {
    public record Image(
        String url,
        int height,
        int width
    ) {}

    public record ExternalUrls(
        String spotify
    ) {}
}
