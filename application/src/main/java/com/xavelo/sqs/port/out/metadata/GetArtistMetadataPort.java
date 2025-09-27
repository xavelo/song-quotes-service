package com.xavelo.sqs.port.out.metadata;

import com.xavelo.sqs.application.domain.Artist;

public interface GetArtistMetadataPort {
    Artist getArtistMetadata(String artistName);
}
