CREATE TABLE quotes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quote VARCHAR(1000),
    song VARCHAR(255),
    album VARCHAR(255),
    album_year INT,
    artist VARCHAR(255),
    posts INT NOT NULL DEFAULT 0,
    hits INT NOT NULL DEFAULT 0
);
