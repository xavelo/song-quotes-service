package com.xavelo.sqs.adapter.out.spotify;

import com.xavelo.sqs.adapter.out.spotify.client.SpotifyArtistClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifyArtistResponse;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchResponse;
import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.port.ArtistMetadataPort;
import org.springframework.stereotype.Component;

@Component
public class SpotifyAdapter implements ArtistMetadataPort {

    private final SpotifySearchClient spotifySearchClient;
    private final SpotifyArtistClient spotifyArtistClient;

    public SpotifyAdapter(SpotifySearchClient spotifySearchClient, SpotifyArtistClient spotifyArtistClient) {
        this.spotifySearchClient = spotifySearchClient;
        this.spotifyArtistClient = spotifyArtistClient;
    }

    @Override
    public Artist getArtistMetadata(String artistName) {
        SpotifySearchResponse searchResponse = spotifySearchClient.searchArtist(artistName);
        if (searchResponse != null && searchResponse.artists() != null && searchResponse.artists().items() != null && !searchResponse.artists().items().isEmpty()) {
            String artistId = searchResponse.artists().items().get(0).id();
            SpotifyArtistResponse artistResponse = spotifyArtistClient.getArtist(artistId);
            if (artistResponse != null) {
                String imageUrl = artistResponse.images() != null && !artistResponse.images().isEmpty() ? artistResponse.images().get(0).url() : null;
                String spotifyUrl = artistResponse.externalUrls() != null ? artistResponse.externalUrls().spotify() : null;
                return new Artist(artistResponse.id(), artistResponse.name(), artistResponse.genres(), artistResponse.popularity(), imageUrl, spotifyUrl);
            }
        }
        return null;
    }
}
