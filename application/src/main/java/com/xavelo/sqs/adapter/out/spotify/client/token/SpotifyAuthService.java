package com.xavelo.sqs.adapter.out.spotify.client.token;

import com.xavelo.sqs.adapter.Adapter;
import com.xavelo.sqs.configuration.spotify.SpotifyProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Base64;

@Adapter
public class SpotifyAuthService {

    private final SpotifyTokenClient spotifyTokenClient;
    private final SpotifyProperties spotifyProperties;

    public SpotifyAuthService(SpotifyTokenClient spotifyTokenClient, SpotifyProperties spotifyProperties) {
        this.spotifyTokenClient = spotifyTokenClient;
        this.spotifyProperties = spotifyProperties;
    }

    public String getAccessToken() {
        String authString = spotifyProperties.getClientId() + ":" + spotifyProperties.getClientSecret();
        String encodedAuthString = Base64.getEncoder().encodeToString(authString.getBytes());
        String authorizationHeader = "Basic " + encodedAuthString;

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");

        SpotifyTokenResponse tokenResponse = spotifyTokenClient.getToken(authorizationHeader, "grant_type=client_credentials");
        return tokenResponse.accessToken();
    }
}
