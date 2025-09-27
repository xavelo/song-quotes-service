package com.xavelo.sqs.adapter.out.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavelo.sqs.adapter.out.mysql.spotify.SpotifyArtistMetadataRepository;
import com.xavelo.sqs.application.domain.Artist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MysqlAdapterTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteMapper quoteMapper;

    @Mock
    private SpotifyArtistMetadataRepository spotifyArtistMetadataRepository;

    private MysqlAdapter mysqlAdapter;

    @BeforeEach
    void setUp() {
        mysqlAdapter = new MysqlAdapter(
                quoteRepository,
                quoteMapper,
                spotifyArtistMetadataRepository,
                new ObjectMapper()
        );
    }

    @Test
    void syncArtistMetadataSavesMetadataBeforeAssigningQuotes() {
        Artist artistMetadata = new Artist(
                "spotify-id",
                "Artist Name",
                java.util.List.of("rock"),
                50,
                "image",
                "spotify:url",
                java.util.List.of()
        );

        when(spotifyArtistMetadataRepository.existsById("spotify-id")).thenReturn(false);

        mysqlAdapter.syncArtistMetadata("Artist Name", artistMetadata);

        InOrder inOrder = inOrder(spotifyArtistMetadataRepository, quoteRepository);
        inOrder.verify(spotifyArtistMetadataRepository).existsById("spotify-id");
        inOrder.verify(spotifyArtistMetadataRepository).save(any());
        inOrder.verify(quoteRepository).assignSpotifyArtistId("Artist Name", "spotify-id");
    }
}
