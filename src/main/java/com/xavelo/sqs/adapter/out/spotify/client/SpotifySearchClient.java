package com.xavelo.sqs.adapter.out.spotify.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.xavelo.sqs.configuration.spotify.SpotifyFeignClientConfiguration;

@FeignClient(name = "spotify-search", configuration = SpotifyFeignClientConfiguration.class)
public interface SpotifySearchClient {

    @GetMapping("/search?type=artist")
    SpotifySearchResponse searchArtist(@RequestParam("q") String artistName);
}