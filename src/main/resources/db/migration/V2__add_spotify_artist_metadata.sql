CREATE TABLE spotify_artist_metadata (
    spotify_artist_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genres VARCHAR(2048),
    popularity INT,
    image_url VARCHAR(255),
    spotify_url VARCHAR(255),
    top_tracks VARCHAR(2048)
);

ALTER TABLE quotes ADD COLUMN spotify_artist_id VARCHAR(255);
ALTER TABLE quotes ADD CONSTRAINT fk_spotify_artist FOREIGN KEY (spotify_artist_id) REFERENCES spotify_artist_metadata(spotify_artist_id);