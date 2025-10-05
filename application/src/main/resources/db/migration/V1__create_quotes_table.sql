CREATE TABLE quotes (
    id CHAR(36) PRIMARY KEY,
    quote VARCHAR(1000),
    song VARCHAR(255),
    album VARCHAR(255),
    album_year INT,
    artist VARCHAR(255),
    posts INT NOT NULL DEFAULT 0,
    hits INT NOT NULL DEFAULT 0
);
