package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.port.out.metadata.GetArtistMetadataPort;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private final GetArtistMetadataPort artistMetadataPort;

    public MetadataService(GetArtistMetadataPort artistMetadataPort) {
        this.artistMetadataPort = artistMetadataPort;
    }

    public Artist getArtistMetadata(String artistName) {
        return artistMetadataPort.getArtistMetadata(artistName);
    }
}