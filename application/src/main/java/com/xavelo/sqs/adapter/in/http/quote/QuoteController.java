package com.xavelo.sqs.adapter.in.http.quote;

import com.xavelo.common.metrics.Adapter;
import com.xavelo.common.metrics.CountAdapterInvocation;
import com.xavelo.sqs.adapter.in.http.quote.mapper.QuoteMapper;
import com.xavelo.sqs.application.api.QuoteApi;
import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.port.in.CountQuotesUseCase;
import com.xavelo.sqs.port.in.GetQuoteUseCase;
import com.xavelo.sqs.port.in.GetQuotesUseCase;
import com.xavelo.sqs.port.in.GetRandomQuoteUseCase;
import com.xavelo.sqs.port.in.GetTop10QuotesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.xavelo.common.metrics.AdapterMetrics.Direction.IN;
import static com.xavelo.common.metrics.AdapterMetrics.Type.HTTP;

@Adapter
@RestController
@RequestMapping("/api")
public class QuoteController implements QuoteApi {

    private final GetQuotesUseCase getQuotesUseCase;
    private final GetQuoteUseCase getQuoteUseCase;
    private final GetRandomQuoteUseCase getRandomQuoteUseCase;
    private final CountQuotesUseCase countQuotesUseCase;
    private final GetTop10QuotesUseCase getTop10QuotesUseCase;
    private final QuoteMapper quoteMapper;

    public QuoteController(GetQuotesUseCase getQuotesUseCase,
                           GetQuoteUseCase getQuoteUseCase,
                           CountQuotesUseCase countQuotesUseCase,
                           GetRandomQuoteUseCase getRandomQuoteUseCase,
                           GetTop10QuotesUseCase getTop10QuotesUseCase,
                           QuoteMapper quoteMapper) {
        this.getQuotesUseCase = getQuotesUseCase;
        this.getQuoteUseCase = getQuoteUseCase;
        this.getRandomQuoteUseCase = getRandomQuoteUseCase;
        this.countQuotesUseCase = countQuotesUseCase;
        this.getTop10QuotesUseCase = getTop10QuotesUseCase;
        this.quoteMapper = quoteMapper;
    }

    @Override
    @CountAdapterInvocation(name = "get-quotes", direction = IN, type = HTTP)
    public ResponseEntity<List<QuoteDto>> getQuotes() {
        List<Quote> quotes = getQuotesUseCase.getQuotes();
        return ResponseEntity.ok(quoteMapper.toDtos(quotes));
    }

    @Override
    @CountAdapterInvocation(name = "count-quotes", direction = IN, type = HTTP)
    public ResponseEntity<Long> getQuotesCount() {
        Long count = countQuotesUseCase.countQuotes();
        return ResponseEntity.ok(count);
    }

    @Override
    @CountAdapterInvocation(name = "random-quote", direction = IN, type = HTTP)
    public ResponseEntity<QuoteDto> getRandomQuote() {
        Quote quote = getRandomQuoteUseCase.getRandomQuote();
        return quote != null
                ? ResponseEntity.ok(quoteMapper.toDto(quote))
                : ResponseEntity.notFound().build();
    }

    @Override
    @CountAdapterInvocation(name = "get-quote", direction = IN, type = HTTP)
    public ResponseEntity<QuoteDto> getQuote(@PathVariable("id") Long id) {
        Quote quote = getQuoteUseCase.getQuote(id);
        return quote != null
                ? ResponseEntity.ok(quoteMapper.toDto(quote))
                : ResponseEntity.notFound().build();
    }

    @Override
    @CountAdapterInvocation(name = "top10-quotes", direction = IN, type = HTTP)
    public ResponseEntity<List<QuoteDto>> getTop10Quotes() {
        List<Quote> quotes = getTop10QuotesUseCase.getTop10Quotes();
        return ResponseEntity.ok(quoteMapper.toDtos(quotes));
    }
}
