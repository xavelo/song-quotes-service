package com.xavelo.sqs.adapter.out.mysql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "quotes")
public class QuoteEntity {

    @Id
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;

    @Column(unique = true, length = 1000)
    private String quote;
    private String song;
    private String album;
    @Column(name = "album_year")
    private Integer year;
    private String artist;
    @Column(nullable = false)
    private Integer posts = 0;
    @Column(nullable = false)
    private Integer hits = 0;
    @Column(name = "spotify_artist_id")
    private String spotifyArtistId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
    }

    public void setSpotifyArtistId(String spotifyArtistId) {
        this.spotifyArtistId = spotifyArtistId;
    }
}
