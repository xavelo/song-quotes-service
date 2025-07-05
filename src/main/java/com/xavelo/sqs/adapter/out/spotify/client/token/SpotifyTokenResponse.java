package com.xavelo.sqs.adapter.out.spotify.client.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") int expiresIn
) {}
