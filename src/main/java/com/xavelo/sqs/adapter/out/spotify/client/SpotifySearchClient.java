package com.xavelo.sqs.adapter.out.spotify.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spotify-search", url = "https://api.spotify.com/v1")
public interface SpotifySearchClient {

    @GetMapping("/search?type=artist")
    SpotifySearchResponse searchArtist(@RequestParam("q") String artistName);
}
