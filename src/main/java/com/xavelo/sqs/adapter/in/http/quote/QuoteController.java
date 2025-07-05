package com.xavelo.sqs.adapter.in.http.quote;

import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.port.in.DeleteQuoteUseCase;
import com.xavelo.sqs.port.in.GetQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuoteUseCase;
import com.xavelo.sqs.port.in.GetRandomQuoteUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.CountQuotesUseCase;
import com.xavelo.sqs.port.in.GetArtistQuoteCountsUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import com.xavelo.sqs.application.domain.ArtistQuoteCount;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api")
public class QuoteController {

    private final StoreQuoteUseCase storeQuoteUseCase;
    private final GetQuotesUseCase getQuotesUseCase;
    private final GetQuoteUseCase getQuoteUseCase;
    private final GetRandomQuoteUseCase getRandomQuoteUseCase;
    private final CountQuotesUseCase countQuotesUseCase;
    private final DeleteQuoteUseCase deleteQuoteUseCase;
    private final GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;

    public QuoteController(StoreQuoteUseCase storeQuoteUseCase,
                           GetQuotesUseCase getQuotesUseCase,
                           GetQuoteUseCase getQuoteUseCase,
                           DeleteQuoteUseCase deleteQuoteUseCase,
                           CountQuotesUseCase countQuotesUseCase,
                           GetRandomQuoteUseCase getRandomQuoteUseCase,
                           GetArtistQuoteCountsUseCase getArtistQuoteCountsUseCase,
                           UpdateQuoteUseCase updateQuoteUseCase) {
        this.storeQuoteUseCase = storeQuoteUseCase;
        this.getQuotesUseCase = getQuotesUseCase;
        this.getQuoteUseCase = getQuoteUseCase;
        this.getRandomQuoteUseCase = getRandomQuoteUseCase;
        this.deleteQuoteUseCase = deleteQuoteUseCase;
        this.countQuotesUseCase = countQuotesUseCase;
        this.getArtistQuoteCountsUseCase = getArtistQuoteCountsUseCase;
        this.updateQuoteUseCase = updateQuoteUseCase;
    }

    @PostMapping("/quote")
    public ResponseEntity<Long> createQuote(@RequestBody Quote quote) {
        Long id = storeQuoteUseCase.storeQuote(quote);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/quotes")
    public ResponseEntity<java.util.List<Long>> createQuotes(@RequestBody java.util.List<Quote> quotes) {
        java.util.List<Long> ids = storeQuoteUseCase.storeQuotes(quotes);
        return ResponseEntity.ok(ids);
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

    @PutMapping("/quote/{id}")
    public ResponseEntity<Void> updateQuote(@PathVariable Long id, @RequestBody Quote quote) {
        updateQuoteUseCase.updateQuote(QuoteHelper.withId(quote, id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quote/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        deleteQuoteUseCase.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }
}
