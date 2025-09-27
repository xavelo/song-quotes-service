package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.port.out.metadata.GetArtistMetadataPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    private static final Logger logger = LogManager.getLogger(MetadataService.class);
    private final GetArtistMetadataPort artistMetadataPort;

    public MetadataService(GetArtistMetadataPort artistMetadataPort) {
        this.artistMetadataPort = artistMetadataPort;
    }

    public Artist getArtistMetadata(String artistName) {
        Artist artistMetadata = artistMetadataPort.getArtistMetadata(artistName);
        logger.debug(artistMetadata);
        return artistMetadata;
    }
}