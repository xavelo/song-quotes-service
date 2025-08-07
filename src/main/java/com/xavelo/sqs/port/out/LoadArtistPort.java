package com.xavelo.sqs.port.out;

import com.xavelo.sqs.application.domain.Artist;

public interface LoadArtistPort {
    Artist loadArtist(String id);
}

