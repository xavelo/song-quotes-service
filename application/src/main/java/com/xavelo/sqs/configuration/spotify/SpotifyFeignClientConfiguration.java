package com.xavelo.sqs.configuration.spotify;

import com.xavelo.sqs.adapter.out.spotify.client.token.SpotifyAuthService;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpotifyFeignClientConfiguration {

    private final SpotifyProperties spotifyProperties;
    private final SpotifyAuthService spotifyAuthService;

    public SpotifyFeignClientConfiguration(SpotifyProperties spotifyProperties, SpotifyAuthService spotifyAuthService) {
        this.spotifyProperties = spotifyProperties;
        this.spotifyAuthService = spotifyAuthService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + spotifyAuthService.getAccessToken());
            requestTemplate.target(spotifyProperties.getUrl());
        };
    }
}
