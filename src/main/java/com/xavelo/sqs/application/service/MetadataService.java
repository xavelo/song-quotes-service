package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.port.ArtistMetadataPort;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final ArtistMetadataPort artistMetadataPort;

    public MetadataService(ArtistMetadataPort artistMetadataPort) {
        this.artistMetadataPort = artistMetadataPort;
    }

    public String getArtistMetadata(String artistName) {
        return artistMetadataPort.getArtistMetadata(artistName);
    }
}
