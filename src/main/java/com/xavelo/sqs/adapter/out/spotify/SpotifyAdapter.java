package com.xavelo.sqs.adapter.out.spotify;

import com.xavelo.sqs.application.port.ArtistMetadataPort;
import org.springframework.stereotype.Component;

@Component
public class SpotifyAdapter implements ArtistMetadataPort {

    @Override
    public String getArtistMetadata(String artistName) {
        // For now, return a lorem ipsum like text
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    }
}
