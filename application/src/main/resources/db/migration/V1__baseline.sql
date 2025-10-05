CREATE TABLE quotes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quote VARCHAR(1000),
    song VARCHAR(255),
    album VARCHAR(255),
    album_year INT,
    artist VARCHAR(255),
    posts INT NOT NULL DEFAULT 0,
    hits INT NOT NULL DEFAULT 0,
    spotify_artist_id VARCHAR(255)
);

CREATE TABLE spotify_artist_metadata (
    spotify_artist_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genres VARCHAR(2048),
    popularity INT,
    image_url VARCHAR(255),
    spotify_url VARCHAR(255),
    top_tracks VARCHAR(2048)
);

ALTER TABLE quotes
    ADD CONSTRAINT fk_spotify_artist
    FOREIGN KEY (spotify_artist_id)
    REFERENCES spotify_artist_metadata(spotify_artist_id);

CREATE TABLE quote_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    payload TEXT NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    last_error TEXT NULL,
    available_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE INDEX idx_quote_events_status_available_at ON quote_events(status, available_at);
