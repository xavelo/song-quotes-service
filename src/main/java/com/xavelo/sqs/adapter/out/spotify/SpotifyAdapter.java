package com.xavelo.sqs.adapter.out.spotify;

import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchResponse;
import com.xavelo.sqs.application.port.ArtistMetadataPort;
import org.springframework.stereotype.Component;

@Component
public class SpotifyAdapter implements ArtistMetadataPort {

    private final SpotifySearchClient spotifySearchClient;

    public SpotifyAdapter(SpotifySearchClient spotifySearchClient) {
        this.spotifySearchClient = spotifySearchClient;
    }

    @Override
    public String getArtistMetadata(String artistName) {
        SpotifySearchResponse response = spotifySearchClient.searchArtist(artistName);
        if (response != null && response.artists() != null && !response.artists().isEmpty()) {
            return response.artists().get(0).id();
        }
        return "Artist not found";
    }
}
