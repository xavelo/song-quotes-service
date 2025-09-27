package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Artist;

public interface SyncArtistMetadataPort {
    void syncArtistMetadata(String artistName, Artist artistMetadata);
}
