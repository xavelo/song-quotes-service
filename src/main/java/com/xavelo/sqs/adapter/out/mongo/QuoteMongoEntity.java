package com.xavelo.sqs.adapter.out.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quotes")
public class QuoteMongoEntity {
    @Id
    private Long id;
    private String quote;
    private String song;
    private String album;
    private Integer year;
    private String artist;
    private Integer hits;
    private Integer posts;

    public QuoteMongoEntity() {
    }

    public QuoteMongoEntity(Long id, String quote, String song, String album, Integer year, String artist, Integer hits, Integer posts) {
        this.id = id;
        this.quote = quote;
        this.song = song;
        this.album = album;
        this.year = year;
        this.artist = artist;
        this.hits = hits;
        this.posts = posts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }
}
