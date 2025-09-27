package com.xavelo.sqs.adapter.out.spotify.client.token;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "spotify-token", url = "https://accounts.spotify.com/api")
public interface SpotifyTokenClient {

    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
    SpotifyTokenResponse getToken(
        @RequestHeader("Authorization") String authorization,
        @RequestBody String requestBody
    );
}
