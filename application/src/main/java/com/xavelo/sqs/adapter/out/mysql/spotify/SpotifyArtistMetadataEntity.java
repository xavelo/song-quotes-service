package com.xavelo.sqs.adapter.out.mysql.spotify;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "spotify_artist_metadata")
public class SpotifyArtistMetadataEntity {

    @Id
    @Column(name = "spotify_artist_id")
    private String spotifyArtistId;
    private String name;
    private String genres;
    private int popularity;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "spotify_url")
    private String spotifyUrl;
    private String topTracks;

    public SpotifyArtistMetadataEntity() {
    }

    public SpotifyArtistMetadataEntity(String spotifyArtistId, String name, String genres, int popularity, String imageUrl, String spotifyUrl, String topTracks) {
        this.spotifyArtistId = spotifyArtistId;
        this.name = name;
        this.genres = genres;
        this.popularity = popularity;
        this.imageUrl = imageUrl;
        this.spotifyUrl = spotifyUrl;
        this.topTracks = topTracks;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
    }

    public void setSpotifyArtistId(String spotifyArtistId) {
        this.spotifyArtistId = spotifyArtistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getTopTracks() {
        return topTracks;
    }

    public void setTopTracks(String topTracks) {
        this.topTracks = topTracks;
    }
}