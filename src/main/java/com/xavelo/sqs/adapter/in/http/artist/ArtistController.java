package com.xavelo.sqs.adapter.in.http.artist;

import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.port.in.GetArtistUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArtistController {

    private final GetArtistUseCase getArtistUseCase;

    public ArtistController(GetArtistUseCase getArtistUseCase) {
        this.getArtistUseCase = getArtistUseCase;
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable String id) {
        Artist artist = getArtistUseCase.getArtist(id);
        return artist != null ? ResponseEntity.ok(artist) : ResponseEntity.notFound().build();
    }
}

