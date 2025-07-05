package com.xavelo.sqs.adapter.in.http.quote;

import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class QuoteController {

    private final GetQuotesUseCase getQuotesUseCase;
    private final GetQuoteUseCase getQuoteUseCase;
    private final GetRandomQuoteUseCase getRandomQuoteUseCase;
    private final CountQuotesUseCase countQuotesUseCase;
    private final GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;
    private final GetTop10QuotesUseCase getTop10QuotesUseCase;

    public QuoteController(GetQuotesUseCase getQuotesUseCase,
                           GetQuoteUseCase getQuoteUseCase,
                           CountQuotesUseCase countQuotesUseCase,
                           GetRandomQuoteUseCase getRandomQuoteUseCase,
                           GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase,
                           GetTop10QuotesUseCase getTop10QuotesUseCase) {
        this.getQuotesUseCase = getQuotesUseCase;
        this.getQuoteUseCase = getQuoteUseCase;
        this.getRandomQuoteUseCase = getRandomQuoteUseCase;
        this.countQuotesUseCase = countQuotesUseCase;
        this.getArtistQuoteCountsUseCase = getArtistQuoteCountsUseCase;
        this.getTop10QuotesUseCase = getTop10QuotesUseCase;
    }

    @GetMapping("/quotes")
    public ResponseEntity<java.util.List<Quote>> getQuotes() {
        java.util.List<Quote> quotes = getQuotesUseCase.getQuotes();
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/artists")
    public ResponseEntity<java.util.List<ArtistQuoteCount>> getArtists() {
        java.util.List<ArtistQuoteCount> artists = getArtistQuoteCountsUseCase.getArtistQuoteCounts();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/quotes/count")
    public ResponseEntity<Long> getQuotesCount() {
        Long count = countQuotesUseCase.countQuotes();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/quote/random")
    public ResponseEntity<Quote> getRandomQuote() {
        Quote quote = getRandomQuoteUseCase.getRandomQuote();
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }

    @GetMapping("/quote/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable Long id) {
        Quote quote = getQuoteUseCase.getQuote(id);
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }

    @GetMapping("/quotes/top10")
    public ResponseEntity<java.util.List<Quote>> getTop10Quotes() {
        java.util.List<Quote> quotes = getTop10QuotesUseCase.getTop10Quotes();
        return ResponseEntity.ok(quotes);
    }
}
