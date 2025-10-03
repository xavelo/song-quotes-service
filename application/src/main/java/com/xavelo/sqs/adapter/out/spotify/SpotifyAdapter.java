package com.xavelo.sqs.adapter.out.spotify;

import com.xavelo.sqs.adapter.CountAdapterInvocation;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifyArtistClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifyArtistResponse;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifySearchResponse;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifyTopTracksClient;
import com.xavelo.sqs.adapter.out.spotify.client.SpotifyTopTracksResponse;
import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.adapter.Adapter;
import com.xavelo.sqs.port.out.metadata.GetArtistMetadataPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.List;

import static com.xavelo.sqs.adapter.AdapterMetrics.Direction.OUT;
import static com.xavelo.sqs.adapter.AdapterMetrics.Type.HTTP;

@Adapter
public class SpotifyAdapter implements GetArtistMetadataPort {

    private static final Logger logger = LogManager.getLogger(SpotifyAdapter.class);

    private final SpotifySearchClient spotifySearchClient;
    private final SpotifyArtistClient spotifyArtistClient;
    private final SpotifyTopTracksClient spotifyTopTracksClient;

    public SpotifyAdapter(SpotifySearchClient spotifySearchClient, SpotifyArtistClient spotifyArtistClient, SpotifyTopTracksClient spotifyTopTracksClient) {
        this.spotifySearchClient = spotifySearchClient;
        this.spotifyArtistClient = spotifyArtistClient;
        this.spotifyTopTracksClient = spotifyTopTracksClient;
    }

    @Override
    @CountAdapterInvocation(name = "get-artist-metadata", direction = OUT, type = HTTP)
    public Artist getArtistMetadata(String artistName) {
        SpotifySearchResponse searchResponse = spotifySearchClient.searchArtist(artistName);
        if (searchResponse != null && searchResponse.artists() != null && searchResponse.artists().items() != null && !searchResponse.artists().items().isEmpty()) {
            String artistId = searchResponse.artists().items().get(0).id();
            SpotifyArtistResponse artistResponse = spotifyArtistClient.getArtist(artistId);
            List<Artist.Track> topTracks = Collections.emptyList();
            SpotifyTopTracksResponse topTracksResponse = spotifyTopTracksClient.getArtistTopTracks(artistId, "US"); // Using US market for now
            if (topTracksResponse != null && topTracksResponse.tracks() != null) {
                topTracks = topTracksResponse.tracks().stream()
                        .map(track -> new Artist.Track(track.id(), track.name(), track.previewUrl()))
                        .toList();
            }

            if (artistResponse != null) {
                String imageUrl = artistResponse.images() != null && !artistResponse.images().isEmpty() ? artistResponse.images().get(0).url() : null;
                String spotifyUrl = artistResponse.externalUrls() != null ? artistResponse.externalUrls().spotify() : null;
                return new Artist(artistResponse.id(), artistResponse.name(), artistResponse.genres(), artistResponse.popularity(), imageUrl, spotifyUrl, topTracks);
            }
        }
        return null;
    }
}
