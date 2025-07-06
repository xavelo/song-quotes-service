package com.xavelo.sqs.adapter.out.mysql.spotify;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyArtistMetadataRepository extends JpaRepository<SpotifyArtistMetadataEntity, String> {
}
