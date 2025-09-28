package com.xavelo.sqs.adapter.in.http.admin;

import com.xavelo.sqs.adapter.in.http.admin.mapper.AdminQuoteMapper;
import com.xavelo.sqs.application.api.AdminApi;
import com.xavelo.sqs.application.api.model.QuoteDto;
import com.xavelo.sqs.application.domain.Quote;
import com.xavelo.sqs.application.service.AdminService;
import com.xavelo.sqs.application.service.QuoteHelper;
import com.xavelo.sqs.port.in.PatchQuoteUseCase;
import com.xavelo.sqs.port.in.ResetQuoteHitsUseCase;
import com.xavelo.sqs.port.in.ResetQuotePostsUseCase;
import com.xavelo.sqs.port.in.StoreQuoteUseCase;
import com.xavelo.sqs.port.in.UpdateQuoteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final StoreQuoteUseCase storeQuoteUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final PatchQuoteUseCase patchQuoteUseCase;
    private final ResetQuoteHitsUseCase resetQuoteHitsUseCase;
    private final ResetQuotePostsUseCase resetQuotePostsUseCase;
    private final AdminQuoteMapper quoteMapper;

    public AdminController(AdminService adminService,
                           StoreQuoteUseCase storeQuoteUseCase,
                           UpdateQuoteUseCase updateQuoteUseCase,
                           PatchQuoteUseCase patchQuoteUseCase,
                           ResetQuoteHitsUseCase resetQuoteHitsUseCase,
                           ResetQuotePostsUseCase resetQuotePostsUseCase,
                           AdminQuoteMapper quoteMapper) {
        this.adminService = adminService;
        this.storeQuoteUseCase = storeQuoteUseCase;
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.patchQuoteUseCase = patchQuoteUseCase;
        this.resetQuoteHitsUseCase = resetQuoteHitsUseCase;
        this.resetQuotePostsUseCase = resetQuotePostsUseCase;
        this.quoteMapper = quoteMapper;
    }

    @Override
    public ResponseEntity<Long> createQuote(@Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        Long id = storeQuoteUseCase.storeQuote(quote);
        return ResponseEntity.ok(id);
    }

    @Override
    public ResponseEntity<List<Long>> createQuotes(@Valid @RequestBody List<@Valid QuoteDto> quoteDtos) {
        List<Quote> quotes = quoteMapper.toDomain(quoteDtos);
        List<Long> ids = storeQuoteUseCase.storeQuotes(quotes);
        return ResponseEntity.ok(ids);
    }

    @Override
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        adminService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateQuote(@PathVariable Long id, @Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        if (containsRestrictedFields(quote)) {
            return ResponseEntity.badRequest().build();
        }
        updateQuoteUseCase.updateQuote(QuoteHelper.withId(quote, id));
        return ResponseEntity.noContent().build();
    }

    private boolean containsRestrictedFields(Quote quote) {
        return quote.posts() != null || quote.hits() != null;
    }

    @Override
    public ResponseEntity<Void> patchQuote(@PathVariable Long id, @Valid @RequestBody QuoteDto quoteDto) {
        Quote quote = quoteMapper.toDomain(quoteDto);
        if (containsRestrictedFields(quote)) {
            return ResponseEntity.badRequest().build();
        }
        patchQuoteUseCase.patchQuote(id, quote);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<String> exportQuotes() {
        String sql = adminService.exportQuotesAsSql();
        return ResponseEntity.ok(sql);
    }

    @Override
    public ResponseEntity<Void> resetQuoteHits() {
        resetQuoteHitsUseCase.resetQuoteHits();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> resetQuotePosts() {
        resetQuotePostsUseCase.resetQuotePosts();
        return ResponseEntity.noContent().build();
    }

}
