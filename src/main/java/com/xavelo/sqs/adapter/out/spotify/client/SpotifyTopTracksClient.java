package com.xavelo.sqs.adapter.out.spotify.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.xavelo.sqs.configuration.spotify.SpotifyFeignClientConfiguration;

@FeignClient(name = "spotify-top-tracks", configuration = SpotifyFeignClientConfiguration.class)
public interface SpotifyTopTracksClient {

    @GetMapping("/artists/{id}/top-tracks")
    SpotifyTopTracksResponse getArtistTopTracks(
        @PathVariable("id") String artistId,
        @RequestParam("market") String market
    );
}
