package com.xavelo.sqs.application.port;

import com.xavelo.sqs.application.domain.Artist;

public interface ArtistMetadataPort {
    Artist getArtistMetadata(String artistName);
}
