package com.xavelo.sqs.adapter.in.http.artist;

import com.xavelo.sqs.adapter.in.http.artist.mapper.ArtistMapper;
import com.xavelo.sqs.application.api.DefaultApi;
import com.xavelo.sqs.application.api.model.ArtistDto;
import com.xavelo.sqs.application.api.model.ArtistQuoteCountDto;
import com.xavelo.sqs.application.domain.Artist;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import com.xavelo.sqs.port.in.GetArtistUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ArtistController implements DefaultApi {

    private final GetArtistUseCase getArtistUseCase;
    private final GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;
    private final ArtistMapper artistMapper;

    public ArtistController(GetArtistUseCase getArtistUseCase, GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase,
                           ArtistMapper artistMapper) {
        this.getArtistUseCase = getArtistUseCase;
        this.getArtistQuoteCountsUseCase = getArtistQuoteCountsUseCase;
        this.artistMapper = artistMapper;
    }

    @Override
    public ResponseEntity<ArtistDto> getArtist(@PathVariable String id) {
        Artist artist = getArtistUseCase.getArtist(id);
        return artist != null
                ? ResponseEntity.ok(artistMapper.toDto(artist))
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<ArtistQuoteCountDto>> getArtists() {
        List<ArtistQuoteCount> artists = getArtistQuoteCountsUseCase.getArtistQuoteCounts();
        return ResponseEntity.ok(artistMapper.toQuoteCountDtos(artists));
    }
}
