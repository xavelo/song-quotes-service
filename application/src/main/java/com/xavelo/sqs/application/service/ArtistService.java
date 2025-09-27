package com.xavelo.sqs.application.service;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.port.in.GetArtistUseCase;
import com.xavelo.sqs.port.out.LoadArtistPort;
import org.springframework.stereotype.Service;

@Service
public class ArtistService implements GetArtistUseCase {

    private final LoadArtistPort loadArtistPort;

    public ArtistService(LoadArtistPort loadArtistPort) {
        this.loadArtistPort = loadArtistPort;
    }

    @Override
    public Artist getArtist(String id) {
        return loadArtistPort.loadArtist(id);
    }
}

