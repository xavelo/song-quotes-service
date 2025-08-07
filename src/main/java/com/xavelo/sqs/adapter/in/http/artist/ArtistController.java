package com.xavelo.sqs.adapter.in.http.artist;

import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArtistController {

    private final GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;

    public ArtistController(GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase) {
        this.getArtistQuoteCountsUseCase = getArtistQuoteCountsUseCase;
    }

    @GetMapping("/artists")
    public ResponseEntity<java.util.List<ArtistQuoteCount>> getArtists() {
        java.util.List<ArtistQuoteCount> artists = getArtistQuoteCountsUseCase.getArtistQuoteCounts();
        return ResponseEntity.ok(artists);
    }
}
