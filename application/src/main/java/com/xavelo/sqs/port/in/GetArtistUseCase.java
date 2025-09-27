package com.xavelo.sqs.port.in;

import com.xavelo.sqs.application.domain.Artist;

public interface GetArtistUseCase {
    Artist getArtist(String id);
}

