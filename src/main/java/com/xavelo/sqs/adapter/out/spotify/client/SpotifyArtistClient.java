package com.xavelo.sqs.adapter.out.spotify.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.xavelo.sqs.configuration.spotify.SpotifyFeignClientConfiguration;

@FeignClient(name = "spotify-artist", configuration = SpotifyFeignClientConfiguration.class)
public interface SpotifyArtistClient {

    @GetMapping("/artists/{id}")
    SpotifyArtistResponse getArtist(@PathVariable("id") String artistId);
}
